# CampusTrace API Documentation

All API requests should be routed through the **API Gateway** on port `8080` (i.e., `http://localhost:8080`).

## Authentication
Most endpoints require a JWT token for access. 
1. Get a token via `POST /api/auth/login`.
2. Include it in the header of subsequent requests:
   `Authorization: Bearer <YOUR_TOKEN>`

---

## 1. Auth & Users (`user-service`)

### Register a User
*   **Endpoint:** `POST /api/auth/register`
*   **Auth Required:** No
*   **Body:**
    ```json
    {
      "username": "johndoe",
      "password": "password123",
      "fullName": "John Doe",
      "email": "john@example.com"
    }
    ```
    *(Note: If `username` contains "admin", the user is granted the `ADMIN` role).*

### Login
*   **Endpoint:** `POST /api/auth/login`
*   **Auth Required:** No
*   **Body:**
    ```json
    {
      "username": "johndoe",
      "password": "password123"
    }
    ```

### Get Current User Profile
*   **Endpoint:** `GET /api/users/me`
*   **Auth Required:** Yes (STUDENT or ADMIN)

---

## 2. Lost Items (`lost-item-service`)

### Report a Lost Item
*   **Endpoint:** `POST /api/lost-items`
*   **Auth Required:** Yes
*   **Body:**
    ```json
    {
      "itemName": "MacBook Air",
      "description": "Silver, 2022 model with a CS sticker",
      "category": "Electronics",
      "location": "Library 2nd Floor"
    }
    ```

### Get All Lost Items (Global Feed)
*   **Endpoint:** `GET /api/lost-items`
*   **Auth Required:** Yes

### Get My Lost Items
*   **Endpoint:** `GET /api/lost-items/my-items`
*   **Auth Required:** Yes

### Delete a Lost Item
*   **Endpoint:** `DELETE /api/lost-items/{id}`
*   **Auth Required:** Yes

---

## 3. Found Items (`found-item-service`)

### Report a Found Item
*   **Endpoint:** `POST /api/found-items`
*   **Auth Required:** Yes
*   **Body:**
    ```json
    {
      "itemName": "Honda Car Keys",
      "description": "Keys with a red lanyard",
      "category": "Accessories",
      "locationFound": "North Parking Lot"
    }
    ```

### Get All Found Items (Global Feed)
*   **Endpoint:** `GET /api/found-items`
*   **Auth Required:** Yes

### Get My Found Items
*   **Endpoint:** `GET /api/found-items/my-items`
*   **Auth Required:** Yes

### Delete a Found Item
*   **Endpoint:** `DELETE /api/found-items/{id}`
*   **Auth Required:** Yes

---

## 4. Claims (`claim-service`)

### Submit a Claim
*   **Endpoint:** `POST /api/claims`
*   **Auth Required:** Yes
*   **Body:**
    ```json
    {
      "itemId": 1,
      "itemType": "FOUND",
      "description": "These are my car keys.",
      "proofOfOwnership": "I have the spare key to prove it."
    }
    ```
    *(Note: `itemType` must be exactly `"LOST"` or `"FOUND"`).*

### Get All Claims (Admin Panel)
*   **Endpoint:** `GET /api/claims`
*   **Auth Required:** Yes

### Get My Claims
*   **Endpoint:** `GET /api/claims/my-claims`
*   **Auth Required:** Yes

### Approve a Claim
*   **Endpoint:** `PUT /api/claims/{id}/approve`
*   **Auth Required:** Yes (**ADMIN ONLY**)

### Reject a Claim
*   **Endpoint:** `PUT /api/claims/{id}/reject`
*   **Auth Required:** Yes (**ADMIN ONLY**)

---

## 5. Notifications (`notification-service`)

### Get My Notifications
*   **Endpoint:** `GET /api/notifications`
*   **Auth Required:** Yes

### Get Unread Notification Count
*   **Endpoint:** `GET /api/notifications/unread-count`
*   **Auth Required:** Yes

### Mark Notification as Read
*   **Endpoint:** `PUT /api/notifications/{id}/read`
*   **Auth Required:** Yes

### Delete Notification
*   **Endpoint:** `DELETE /api/notifications/{id}`
*   **Auth Required:** Yes
