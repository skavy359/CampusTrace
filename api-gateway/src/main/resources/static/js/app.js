

const API_BASE = '';

const Auth = {
    getToken() {
        return localStorage.getItem('jwt_token');
    },
    setToken(token) {
        localStorage.setItem('jwt_token', token);
    },
    getUser() {
        const user = localStorage.getItem('user_data');
        return user ? JSON.parse(user) : null;
    },
    setUser(userData) {
        localStorage.setItem('user_data', JSON.stringify(userData));
    },
    isLoggedIn() {
        return !!this.getToken();
    },
    logout() {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_data');
        window.location.href = '/login';
    },
    getHeaders() {
        const headers = { 'Content-Type': 'application/json' };
        const token = this.getToken();
        if (token) {
            headers['Authorization'] = 'Bearer ' + token;
        }
        return headers;
    }
};

function showToast(message, type = 'info') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = 'toast toast-' + type;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100px)';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

async function apiCall(url, method = 'GET', body = null) {
    const options = {
        method,
        headers: Auth.getHeaders()
    };
    if (body) {
        options.body = JSON.stringify(body);
    }
    const response = await fetch(API_BASE + url, options);
    if (response.status === 401 || response.status === 403) {
        Auth.logout();
        return null;
    }
    if (response.status === 204) return null;
    const data = await response.json();
    if (!response.ok) {
        throw data;
    }
    return data;
}

async function handleLogin(event) {
    event.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const btn = event.target.querySelector('button[type="submit"]');
    btn.innerHTML = '<span class="spinner"></span> Logging in...';
    btn.disabled = true;

    try {
        const data = await apiCall('/api/auth/login', 'POST', { username, password });
        Auth.setToken(data.token);
        Auth.setUser({ username: data.username, role: data.role });
        showToast('Login successful!', 'success');
        setTimeout(() => window.location.href = '/dashboard', 500);
    } catch (error) {
        showToast(error.message || 'Invalid username or password', 'error');
        btn.innerHTML = 'Sign In';
        btn.disabled = false;
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const fullName = document.getElementById('fullName').value;
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
        showToast('Passwords do not match', 'error');
        return;
    }

    const btn = event.target.querySelector('button[type="submit"]');
    btn.innerHTML = '<span class="spinner"></span> Creating account...';
    btn.disabled = true;

    try {
        const data = await apiCall('/api/auth/register', 'POST', {
            fullName, username, email, password
        });
        Auth.setToken(data.token);
        Auth.setUser({ username: data.username, role: data.role });
        showToast('Registration successful!', 'success');
        setTimeout(() => window.location.href = '/dashboard', 500);
    } catch (error) {
        let msg = 'Registration failed';
        if (typeof error === 'object') {
            msg = Object.values(error).join(', ');
        }
        showToast(msg, 'error');
        btn.innerHTML = 'Create Account';
        btn.disabled = false;
    }
}

async function loadDashboard() {
    checkAuth();
    updateSidebar();
    try {
        const [lostItems, foundItems, claims] = await Promise.all([
            apiCall('/api/lost-items').catch(() => []),
            apiCall('/api/found-items').catch(() => []),
            apiCall('/api/claims').catch(() => [])
        ]);

        document.getElementById('totalLost').textContent = lostItems.length || 0;
        document.getElementById('totalFound').textContent = foundItems.length || 0;
        document.getElementById('totalClaims').textContent = claims.length || 0;
        const pendingClaims = claims.filter(c => c.status === 'PENDING');
        document.getElementById('pendingClaims').textContent = pendingClaims.length || 0;

        const recentContainer = document.getElementById('recentItems');
        if (recentContainer) {
            const allItems = [
                ...lostItems.map(i => ({ ...i, type: 'LOST' })),
                ...foundItems.map(i => ({ ...i, type: 'FOUND' }))
            ].sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0)).slice(0, 6);

            if (allItems.length === 0) {
                recentContainer.innerHTML = '<div class="empty-state"><div class="icon">📦</div><h3>No items yet</h3><p>Start by reporting a lost or found item.</p></div>';
            } else {
                recentContainer.innerHTML = allItems.map(item => createItemCard(item)).join('');
            }
        }
    } catch (error) {
        console.error('Error loading dashboard:', error);
    }
}

async function loadLostItems() {
    checkAuth();
    updateSidebar();
    try {
        const items = await apiCall('/api/lost-items');
        renderItems(items, 'itemsContainer', 'LOST');
    } catch (error) {
        showToast('Failed to load lost items', 'error');
    }
}

async function searchLostItems() {
    const keyword = document.getElementById('searchInput').value.trim();
    if (!keyword) {
        loadLostItems();
        return;
    }
    try {
        const items = await apiCall('/api/lost-items/search?keyword=' + encodeURIComponent(keyword));
        renderItems(items, 'itemsContainer', 'LOST');
    } catch (error) {
        showToast('Search failed', 'error');
    }
}

async function handleAddLostItem(event) {
    event.preventDefault();
    const dto = {
        itemName: document.getElementById('itemName').value,
        description: document.getElementById('description').value,
        category: document.getElementById('category').value,
        location: document.getElementById('location').value,
        dateLost: document.getElementById('dateLost').value || null,
        contactInfo: document.getElementById('contactInfo').value
    };

    try {
        await apiCall('/api/lost-items', 'POST', dto);
        showToast('Lost item reported successfully!', 'success');
        setTimeout(() => window.location.href = '/lost-items', 1000);
    } catch (error) {
        let msg = 'Failed to add item';
        if (typeof error === 'object') msg = Object.values(error).join(', ');
        showToast(msg, 'error');
    }
}

async function loadFoundItems() {
    checkAuth();
    updateSidebar();
    try {
        const items = await apiCall('/api/found-items');
        renderItems(items, 'itemsContainer', 'FOUND');
    } catch (error) {
        showToast('Failed to load found items', 'error');
    }
}

async function searchFoundItems() {
    const keyword = document.getElementById('searchInput').value.trim();
    if (!keyword) {
        loadFoundItems();
        return;
    }
    try {
        const items = await apiCall('/api/found-items/search?keyword=' + encodeURIComponent(keyword));
        renderItems(items, 'itemsContainer', 'FOUND');
    } catch (error) {
        showToast('Search failed', 'error');
    }
}

async function handleAddFoundItem(event) {
    event.preventDefault();
    const dto = {
        itemName: document.getElementById('itemName').value,
        description: document.getElementById('description').value,
        category: document.getElementById('category').value,
        locationFound: document.getElementById('locationFound').value,
        dateFound: document.getElementById('dateFound').value || null,
        contactInfo: document.getElementById('contactInfo').value
    };

    try {
        await apiCall('/api/found-items', 'POST', dto);
        showToast('Found item reported successfully!', 'success');
        setTimeout(() => window.location.href = '/found-items', 1000);
    } catch (error) {
        let msg = 'Failed to add item';
        if (typeof error === 'object') msg = Object.values(error).join(', ');
        showToast(msg, 'error');
    }
}

async function handleSubmitClaim(event) {
    event.preventDefault();
    const dto = {
        itemId: parseInt(document.getElementById('itemId').value),
        itemType: document.getElementById('itemType').value,
        description: document.getElementById('description').value,
        proofOfOwnership: document.getElementById('proofOfOwnership').value
    };

    try {
        await apiCall('/api/claims', 'POST', dto);
        showToast('Claim submitted successfully!', 'success');
        setTimeout(() => window.location.href = '/dashboard', 1000);
    } catch (error) {
        let msg = 'Failed to submit claim';
        if (typeof error === 'object') msg = Object.values(error).join(', ');
        showToast(msg, 'error');
    }
}

async function approveClaim(id) {
    try {
        await apiCall('/api/claims/' + id + '/approve', 'PUT');
        showToast('Claim approved', 'success');
        loadAdminDashboard();
    } catch (error) {
        showToast('Failed to approve claim', 'error');
    }
}

async function rejectClaim(id) {
    try {
        await apiCall('/api/claims/' + id + '/reject', 'PUT');
        showToast('Claim rejected', 'success');
        loadAdminDashboard();
    } catch (error) {
        showToast('Failed to reject claim', 'error');
    }
}

async function loadNotifications() {
    checkAuth();
    updateSidebar();
    try {
        const notifications = await apiCall('/api/notifications');
        const container = document.getElementById('notificationList');
        if (!notifications || notifications.length === 0) {
            container.innerHTML = '<div class="empty-state"><div class="icon">🔔</div><h3>No notifications</h3><p>You\'re all caught up!</p></div>';
            return;
        }
        container.innerHTML = notifications.map(n => `
            <div class="notification-item ${n.read ? '' : 'unread'}">
                <div class="notif-icon ${getNotifIconClass(n.type)}">${getNotifIcon(n.type)}</div>
                <div class="notif-content">
                    <div class="notif-message">${n.message}</div>
                    <div class="notif-time">${formatDate(n.createdAt)}</div>
                </div>
                <div class="notif-actions">
                    ${!n.read ? `<button class="btn btn-sm btn-secondary" onclick="markNotificationRead(${n.id})">Mark Read</button>` : ''}
                    <button class="btn btn-sm btn-danger" onclick="deleteNotification(${n.id})">✕</button>
                </div>
            </div>
        `).join('');
    } catch (error) {
        showToast('Failed to load notifications', 'error');
    }
}

async function markNotificationRead(id) {
    try {
        await apiCall('/api/notifications/' + id + '/read', 'PUT');
        loadNotifications();
    } catch (error) {
        showToast('Failed to mark as read', 'error');
    }
}

async function markAllNotificationsRead() {
    try {
        await apiCall('/api/notifications/read-all', 'PUT');
        showToast('All notifications marked as read', 'success');
        loadNotifications();
    } catch (error) {
        showToast('Failed to mark all as read', 'error');
    }
}

async function deleteNotification(id) {
    try {
        await apiCall('/api/notifications/' + id, 'DELETE');
        showToast('Notification deleted', 'success');
        loadNotifications();
    } catch (error) {
        showToast('Failed to delete notification', 'error');
    }
}

async function loadAdminDashboard() {
    checkAuth();
    updateSidebar();
    const user = Auth.getUser();
    if (!user || user.role !== 'ADMIN') {
        showToast('Access denied. Admin only.', 'error');
        window.location.href = '/dashboard';
        return;
    }

    try {
        const [claims, users, lostItems, foundItems] = await Promise.all([
            apiCall('/api/claims').catch(() => []),
            apiCall('/api/users').catch(() => []),
            apiCall('/api/lost-items').catch(() => []),
            apiCall('/api/found-items').catch(() => [])
        ]);

        document.getElementById('totalUsers').textContent = users.length || 0;
        document.getElementById('totalLost').textContent = lostItems.length || 0;
        document.getElementById('totalFound').textContent = foundItems.length || 0;
        document.getElementById('totalClaims').textContent = claims.length || 0;

        const claimsBody = document.getElementById('claimsTableBody');
        if (claims.length === 0) {
            claimsBody.innerHTML = '<tr><td colspan="6" style="text-align:center;color:var(--text-muted);padding:40px;">No claims found</td></tr>';
        } else {
            claimsBody.innerHTML = claims.map(c => `
                <tr>
                    <td>#${c.id}</td>
                    <td>${c.claimedBy}</td>
                    <td>${c.itemType} #${c.itemId}</td>
                    <td><span class="badge badge-${c.status.toLowerCase()}">${c.status}</span></td>
                    <td>${formatDate(c.createdAt)}</td>
                    <td>
                        ${c.status === 'PENDING' ? `
                            <button class="btn btn-sm btn-success" onclick="approveClaim(${c.id})">Approve</button>
                            <button class="btn btn-sm btn-danger" onclick="rejectClaim(${c.id})">Reject</button>
                        ` : '-'}
                    </td>
                </tr>
            `).join('');
        }

        const usersBody = document.getElementById('usersTableBody');
        if (usersBody && users.length > 0) {
            usersBody.innerHTML = users.map(u => `
                <tr>
                    <td>#${u.id}</td>
                    <td>${u.username}</td>
                    <td>${u.email}</td>
                    <td>${u.fullName}</td>
                    <td><span class="badge badge-${u.role === 'ADMIN' ? 'approved' : 'found'}">${u.role}</span></td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading admin dashboard:', error);
        showToast('Failed to load admin data', 'error');
    }
}

async function deleteLostItem(id) {
    if (!confirm('Are you sure you want to delete this item?')) return;
    try {
        await apiCall('/api/lost-items/' + id, 'DELETE');
        showToast('Item deleted', 'success');
        loadLostItems();
    } catch (error) {
        showToast('Failed to delete item', 'error');
    }
}

async function deleteFoundItem(id) {
    if (!confirm('Are you sure you want to delete this item?')) return;
    try {
        await apiCall('/api/found-items/' + id, 'DELETE');
        showToast('Item deleted', 'success');
        loadFoundItems();
    } catch (error) {
        showToast('Failed to delete item', 'error');
    }
}

function checkAuth() {
    if (!Auth.isLoggedIn()) {
        window.location.href = '/login';
    }
}

function updateSidebar() {
    const user = Auth.getUser();
    if (user) {
        const nameEl = document.getElementById('sidebarUserName');
        const roleEl = document.getElementById('sidebarUserRole');
        const avatarEl = document.getElementById('sidebarUserAvatar');
        if (nameEl) nameEl.textContent = user.username;
        if (roleEl) roleEl.textContent = user.role;
        if (avatarEl) avatarEl.textContent = user.username.charAt(0).toUpperCase();

        const adminNav = document.getElementById('adminNav');
        if (adminNav) {
            adminNav.style.display = user.role === 'ADMIN' ? 'flex' : 'none';
        }
    }

    const path = window.location.pathname;
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
        if (item.getAttribute('href') === path || item.getAttribute('data-page') === path) {
            item.classList.add('active');
        }
    });

    loadUnreadCount();
}

async function loadUnreadCount() {
    try {
        const data = await apiCall('/api/notifications/unread-count');
        const badge = document.getElementById('notifBadge');
        if (badge && data) {
            badge.textContent = data.count || 0;
            badge.style.display = data.count > 0 ? 'flex' : 'none';
        }
    } catch (e) {
        
    }
}

function renderItems(items, containerId, type) {
    const container = document.getElementById(containerId);
    if (!items || items.length === 0) {
        container.innerHTML = `<div class="empty-state"><div class="icon">${type === 'LOST' ? '🔍' : '📦'}</div><h3>No ${type.toLowerCase()} items</h3><p>No items to display.</p></div>`;
        return;
    }
    container.innerHTML = items.map(item => createItemCard({ ...item, type })).join('');
}

function createItemCard(item) {
    const user = Auth.getUser();
    const isOwner = user && user.username === (item.reportedBy || item.foundBy);
    const type = item.type || item.status;
    const badgeClass = type === 'LOST' ? 'badge-lost' : type === 'FOUND' ? 'badge-found' : 'badge-claimed';

    return `
        <div class="item-card">
            <div class="item-header">
                <div class="item-name">${item.itemName}</div>
                <span class="badge ${badgeClass}">${type}</span>
            </div>
            <div class="item-description">${item.description || 'No description provided'}</div>
            <div class="item-meta">
                ${item.category ? `<span class="meta-tag">📁 ${item.category}</span>` : ''}
                ${item.location || item.locationFound ? `<span class="meta-tag">📍 ${item.location || item.locationFound}</span>` : ''}
                ${item.dateLost || item.dateFound ? `<span class="meta-tag">📅 ${item.dateLost || item.dateFound}</span>` : ''}
                ${item.reportedBy || item.foundBy ? `<span class="meta-tag">👤 ${item.reportedBy || item.foundBy}</span>` : ''}
            </div>
            <div class="item-actions">
                <a href="/claim-item?itemId=${item.id}&itemType=${type}" class="btn btn-sm btn-primary">Claim</a>
                ${isOwner ? `<button class="btn btn-sm btn-danger" onclick="delete${type === 'LOST' ? 'Lost' : 'Found'}Item(${item.id})">Delete</button>` : ''}
            </div>
        </div>
    `;
}

function getNotifIcon(type) {
    switch (type) {
        case 'CLAIM_SUBMITTED': return '📋';
        case 'CLAIM_APPROVED': return '✅';
        case 'CLAIM_REJECTED': return '❌';
        default: return 'ℹ️';
    }
}

function getNotifIconClass(type) {
    switch (type) {
        case 'CLAIM_APPROVED': return 'stat-icon green';
        case 'CLAIM_REJECTED': return 'stat-icon red';
        default: return 'stat-icon blue';
    }
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', {
        year: 'numeric', month: 'short', day: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}

function prefillClaimForm() {
    checkAuth();
    updateSidebar();
    const params = new URLSearchParams(window.location.search);
    const itemId = params.get('itemId');
    const itemType = params.get('itemType');
    if (itemId) document.getElementById('itemId').value = itemId;
    if (itemType) document.getElementById('itemType').value = itemType;
}
