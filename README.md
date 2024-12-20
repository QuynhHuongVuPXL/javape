[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/hSJbCunW)
# PXL Activity Tracker

Welcome to the **PXL Activity Tracker** project! 
This template provides a basic starting point to implement the functionality 
required for the activity tracking system. 

---

## Health Endpoint

The template includes a pre-configured **health endpoint** to verify the application's status and the availability of the database connection.

### **Endpoint: `/health`**

#### **Request**
- **Method**: `GET`
- **URL**: `/health`

#### **Response**
The endpoint provides a JSON response indicating the current status of the application and the database connection.

##### **Example Response**
```json
{
    "dbConnection": true,
    "db": "H2",
    "status": "UP",
    "timestamp": "24/11/2024 14:12:53"
}
```

## Project Objective
The goal of the PXL Activity Tracker project is to 
create a system for tracking activities. The project requirements can be found on blackboard.

Good luck! 🚀
