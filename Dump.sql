-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: delivery_system
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customer_id` varchar(50) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `address` text,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `customers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES ('CUST_1763307878955','USR_1763307878953','Adra, Tench Bhatta'),('CUST001','CUST001','House 123, Street 5, Rawalpindi');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_assignments`
--

DROP TABLE IF EXISTS `delivery_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_assignments` (
  `assignment_id` varchar(50) NOT NULL,
  `order_id` varchar(50) NOT NULL,
  `rider_id` varchar(50) NOT NULL,
  `warehouse_staff_id` varchar(50) DEFAULT NULL,
  `assignment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `confirmed_date` timestamp NULL DEFAULT NULL,
  `completed_date` timestamp NULL DEFAULT NULL,
  `status` enum('Pending','Confirmed','In Progress','Completed','Failed') DEFAULT 'Pending',
  `delivery_attempts` int DEFAULT '0',
  PRIMARY KEY (`assignment_id`),
  KEY `order_id` (`order_id`),
  KEY `rider_id` (`rider_id`),
  KEY `warehouse_staff_id` (`warehouse_staff_id`),
  CONSTRAINT `delivery_assignments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `delivery_assignments_ibfk_2` FOREIGN KEY (`rider_id`) REFERENCES `delivery_riders` (`rider_id`),
  CONSTRAINT `delivery_assignments_ibfk_3` FOREIGN KEY (`warehouse_staff_id`) REFERENCES `warehouse_staff` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_assignments`
--

LOCK TABLES `delivery_assignments` WRITE;
/*!40000 ALTER TABLE `delivery_assignments` DISABLE KEYS */;
INSERT INTO `delivery_assignments` VALUES ('DELY_1763480759551','ORD_1763307949621','DRID001','WRHS001','2025-11-18 15:45:59','2025-11-18 16:07:22','2025-11-18 16:17:04','Completed',0);
/*!40000 ALTER TABLE `delivery_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_riders`
--

DROP TABLE IF EXISTS `delivery_riders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_riders` (
  `rider_id` varchar(50) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `vehicle_type` varchar(50) DEFAULT NULL,
  `license_number` varchar(50) DEFAULT NULL,
  `availability_status` enum('Available','Busy','Offline') DEFAULT 'Available',
  `rating` decimal(3,2) DEFAULT '5.00',
  PRIMARY KEY (`rider_id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `delivery_riders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_riders`
--

LOCK TABLES `delivery_riders` WRITE;
/*!40000 ALTER TABLE `delivery_riders` DISABLE KEYS */;
INSERT INTO `delivery_riders` VALUES ('DRID001','DRID001','Motorcycle','ISB-2024-67890','Busy',5.00);
/*!40000 ALTER TABLE `delivery_riders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dispatchers`
--

DROP TABLE IF EXISTS `dispatchers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dispatchers` (
  `dispatcher_id` varchar(50) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `assigned_zone` varchar(100) DEFAULT NULL,
  `availability_status` enum('Available','Busy','Offline') DEFAULT 'Available',
  PRIMARY KEY (`dispatcher_id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `dispatchers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dispatchers`
--

LOCK TABLES `dispatchers` WRITE;
/*!40000 ALTER TABLE `dispatchers` DISABLE KEYS */;
INSERT INTO `dispatchers` VALUES ('DISP001','DISP001','Rawalpindi/Islamabad','Available');
/*!40000 ALTER TABLE `dispatchers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `managers`
--

DROP TABLE IF EXISTS `managers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `managers` (
  `manager_id` varchar(50) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `department` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`manager_id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `managers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `managers`
--

LOCK TABLES `managers` WRITE;
/*!40000 ALTER TABLE `managers` DISABLE KEYS */;
INSERT INTO `managers` VALUES ('MGR001','MGR001','Operations');
/*!40000 ALTER TABLE `managers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `notification_id` varchar(50) NOT NULL,
  `customer_id` varchar(50) NOT NULL,
  `order_id` varchar(50) DEFAULT NULL,
  `message` text NOT NULL,
  `type` enum('info','success','warning') DEFAULT 'info',
  `is_read` tinyint(1) DEFAULT '0',
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notification_id`),
  KEY `customer_id` (`customer_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE CASCADE,
  CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES ('NOTIF_1763300162081','CUST001','ORD_1763300161981','Your delivery request has been submitted successfully! Order ID: ORD_1763300161981. Status: Pending. Manager will review your request soon.','success',1,'2025-11-16 13:36:02'),('NOTIF_1763307949664','CUST_1763307878955','ORD_1763307949621','Your delivery request has been submitted successfully! Order ID: ORD_1763307949621. Status: Pending. Manager will review your request soon.','success',1,'2025-11-16 15:45:49'),('NOTIF_1763320798883','CUST_1763307878955','ORD_1763307949621','Great news! Your order ORD_1763307949621 has been APPROVED! A dispatcher will assign a rider for pickup soon.','success',1,'2025-11-16 19:19:58'),('NOTIF_1763358507678','CUST001','ORD_1763300161981','Your order ORD_1763300161981 has been REJECTED. Reason: wdsfkugasdkujf. You can drop off the parcel at our office instead.','warning',0,'2025-11-17 05:48:27'),('NOTIF_1763401562265','CUST_1763307878955','ORD_1763307949621','A pickup rider has been assigned to collect your parcel! Order: ORD_1763307949621. The rider will arrive soon.','info',1,'2025-11-17 17:46:02'),('NOTIF_1763464286695','CUST_1763307878955','ORD_1763307949621','Your parcel has been collected! Order: ORD_1763307949621. It\'s on its way to the warehouse for processing.','success',1,'2025-11-18 11:11:26'),('NOTIF_1763473151962','CUST001','ORD_1763298472135','Your parcel has been collected! Order: ORD_1763298472135. It\'s on its way to the warehouse for processing.','success',0,'2025-11-18 13:39:11'),('NOTIF_1763480759580','CUST_1763307878955','ORD_1763307949621','Your parcel is out for delivery! Order: ORD_1763307949621. The delivery rider will arrive soon at: asdfasdf','info',1,'2025-11-18 15:45:59'),('NOTIF_1763482624193','CUST_1763307878955','ORD_1763307949621','✅ Your parcel has been delivered! Order: ORD_1763307949621. Thank you for using our delivery service!','success',1,'2025-11-18 16:17:04');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` varchar(50) NOT NULL,
  `customer_id` varchar(50) NOT NULL,
  `sender_name` varchar(100) NOT NULL,
  `sender_phone` varchar(20) DEFAULT NULL,
  `sender_address` text NOT NULL,
  `receiver_name` varchar(100) NOT NULL,
  `receiver_phone` varchar(20) DEFAULT NULL,
  `receiver_address` text NOT NULL,
  `status` enum('Pending','Approved','Rejected','Assigned','Picked Up','In Transit','Delivered') DEFAULT 'Pending',
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `approved_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES ('ORD_1763298472135','CUST001','john_doe','0300-1234567','House 123, Street 5, Rawalpindi','fadsfad','0305-5146469','adsfqwrfasdf	rwasf','Delivered','2025-11-16 13:07:52',NULL),('ORD_1763300161981','CUST001','john_doe','0300-1234567','House 123, Street 5, Rawalpindi','abid','03055146469','k,jasdfioluhasfpoiuWRE','Delivered','2025-11-16 13:36:02','MGR001'),('ORD_1763307949621','CUST_1763307878955','hannan','0300-1234523','Adra, Tench Bhatta','Abid','0300-8501397','asdfasdf','Delivered','2025-11-16 15:45:49','MGR001');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parcels`
--

DROP TABLE IF EXISTS `parcels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parcels` (
  `parcel_id` varchar(50) NOT NULL,
  `order_id` varchar(50) NOT NULL,
  `weight` decimal(10,2) DEFAULT NULL,
  `dimensions` varchar(100) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`parcel_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `parcels_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parcels`
--

LOCK TABLES `parcels` WRITE;
/*!40000 ALTER TABLE `parcels` DISABLE KEYS */;
INSERT INTO `parcels` VALUES ('PCL_1763298472140','ORD_1763298472135',2.50,'30x20x10',''),('PCL_1763300161985','ORD_1763300161981',2.00,'30X20X10','ASFAEWRFASFD'),('PCL_1763307949621','ORD_1763307949621',1.00,'2x2x2','');
/*!40000 ALTER TABLE `parcels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pickup_assignments`
--

DROP TABLE IF EXISTS `pickup_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pickup_assignments` (
  `assignment_id` varchar(50) NOT NULL,
  `order_id` varchar(50) NOT NULL,
  `dispatcher_id` varchar(50) NOT NULL,
  `rider_id` varchar(50) NOT NULL,
  `assignment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `confirmed_date` timestamp NULL DEFAULT NULL,
  `completed_date` timestamp NULL DEFAULT NULL,
  `status` enum('Pending','Confirmed','In Progress','Completed','Rejected') DEFAULT 'Pending',
  PRIMARY KEY (`assignment_id`),
  KEY `order_id` (`order_id`),
  KEY `dispatcher_id` (`dispatcher_id`),
  KEY `rider_id` (`rider_id`),
  CONSTRAINT `pickup_assignments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `pickup_assignments_ibfk_2` FOREIGN KEY (`dispatcher_id`) REFERENCES `dispatchers` (`dispatcher_id`),
  CONSTRAINT `pickup_assignments_ibfk_3` FOREIGN KEY (`rider_id`) REFERENCES `pickup_riders` (`rider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pickup_assignments`
--

LOCK TABLES `pickup_assignments` WRITE;
/*!40000 ALTER TABLE `pickup_assignments` DISABLE KEYS */;
INSERT INTO `pickup_assignments` VALUES ('ASG_1763401562241','ORD_1763307949621','DISP001','PRID001','2025-11-17 17:46:02','2025-11-18 09:34:57','2025-11-18 11:11:26','Completed'),('PA1763387871580','ORD_1763298472135','DISP001','PRID001','2025-11-17 13:57:51','2025-11-18 13:39:06','2025-11-18 13:39:11','Completed');
/*!40000 ALTER TABLE `pickup_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pickup_riders`
--

DROP TABLE IF EXISTS `pickup_riders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pickup_riders` (
  `rider_id` varchar(50) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `vehicle_type` varchar(50) DEFAULT NULL,
  `license_number` varchar(50) DEFAULT NULL,
  `availability_status` enum('Available','Busy','Offline') DEFAULT 'Available',
  `rating` decimal(3,2) DEFAULT '5.00',
  PRIMARY KEY (`rider_id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `pickup_riders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pickup_riders`
--

LOCK TABLES `pickup_riders` WRITE;
/*!40000 ALTER TABLE `pickup_riders` DISABLE KEYS */;
INSERT INTO `pickup_riders` VALUES ('PRID001','PRID001','Motorcycle','ISB-2024-12345','Busy',5.00);
/*!40000 ALTER TABLE `pickup_riders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipment_pictures`
--

DROP TABLE IF EXISTS `shipment_pictures`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipment_pictures` (
  `picture_id` varchar(50) NOT NULL,
  `order_id` varchar(50) NOT NULL,
  `image_path` varchar(255) NOT NULL,
  `upload_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`picture_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `shipment_pictures_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipment_pictures`
--

LOCK TABLES `shipment_pictures` WRITE;
/*!40000 ALTER TABLE `shipment_pictures` DISABLE KEYS */;
INSERT INTO `shipment_pictures` VALUES ('PIC_1763298472257','ORD_1763298472135','uploads/parcel_1763298467691.jpg','2025-11-16 13:07:52'),('PIC_1763300162069','ORD_1763300161981','uploads/parcel_1763300155679.jpg','2025-11-16 13:36:02'),('PIC_1763307949653','ORD_1763307949621','uploads/parcel_1763307943262.jpg','2025-11-16 15:45:49');
/*!40000 ALTER TABLE `shipment_pictures` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status_history`
--

DROP TABLE IF EXISTS `status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status_history` (
  `history_id` varchar(50) NOT NULL,
  `order_id` varchar(50) NOT NULL,
  `old_status` varchar(50) DEFAULT NULL,
  `new_status` varchar(50) NOT NULL,
  `changed_by` varchar(50) DEFAULT NULL,
  `change_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `notes` text,
  PRIMARY KEY (`history_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `status_history_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status_history`
--

LOCK TABLES `status_history` WRITE;
/*!40000 ALTER TABLE `status_history` DISABLE KEYS */;
INSERT INTO `status_history` VALUES ('HIST_1763300162074','ORD_1763300161981',NULL,'Pending','CUST001','2025-11-16 13:36:02','Order created by customer'),('HIST_1763307949654','ORD_1763307949621',NULL,'Pending','CUST_1763307878955','2025-11-16 15:45:49','Order created by customer'),('HIST_1763320798879','ORD_1763307949621','Pending','Approved','MGR001','2025-11-16 19:19:58','Order approved by manager'),('HIST_1763358507669','ORD_1763300161981','Pending','Rejected','MGR001','2025-11-17 05:48:27','Order rejected by manager. Reason: wdsfkugasdkujf'),('HIST_1763401562263','ORD_1763307949621','Approved','Assigned','DISP001','2025-11-17 17:46:02','Pickup rider assigned by dispatcher'),('HIST_1763464286684','ORD_1763307949621','Pickup Assigned','Collected','ASG_1763401562241','2025-11-18 11:11:26','Parcel collected by pickup rider'),('HIST_1763473151960','ORD_1763298472135','Pickup Assigned','Collected','PA1763387871580','2025-11-18 13:39:11','Parcel collected by pickup rider'),('HIST_1763480759578','ORD_1763307949621','Collected','Out for Delivery','WRHS001','2025-11-18 15:45:59','Delivery rider assigned from warehouse'),('HIST_1763482624189','ORD_1763307949621','Out for Delivery','Delivered','DELY_1763480759551','2025-11-18 16:17:04','Parcel delivered successfully');
/*!40000 ALTER TABLE `status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` varchar(50) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` enum('customer','manager','dispatcher','rider','admin','pickup_rider','delivery_rider','warehouse_staff') NOT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('ADM001','admin','admin123','admin@delivery.com','0306-1234567','admin','2025-11-17 13:08:36'),('CUST001','john_doe','password123','john@example.com','0300-1234567','customer','2025-11-15 19:32:49'),('DISP001','dispatcher','disp123','dispatcher@delivery.com','0302-1234567','dispatcher','2025-11-17 13:05:45'),('DRID001','delivery_rider','delivery123','delivery@delivery.com','0304-1234567','delivery_rider','2025-11-17 13:08:36'),('MGR001','manager','manager123','manager@delivery.com','0301-1234567','manager','2025-11-16 19:08:41'),('PRID001','pickup_rider','rider123','pickup@delivery.com','0303-1234567','pickup_rider','2025-11-17 13:08:36'),('USR_1763307878953','hannan','123','123@123.com','0300-1234523','customer','2025-11-16 15:44:38'),('WRHS001','warehouse','warehouse123','warehouse@delivery.com','0305-1234567','warehouse_staff','2025-11-17 13:08:36');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse_staff`
--

DROP TABLE IF EXISTS `warehouse_staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warehouse_staff` (
  `staff_id` varchar(50) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `warehouse_id` varchar(50) DEFAULT NULL,
  `shift` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`staff_id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `warehouse_staff_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse_staff`
--

LOCK TABLES `warehouse_staff` WRITE;
/*!40000 ALTER TABLE `warehouse_staff` DISABLE KEYS */;
INSERT INTO `warehouse_staff` VALUES ('WRHS001','WRHS001','WH001','Morning');
/*!40000 ALTER TABLE `warehouse_staff` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'delivery_system'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-18 22:52:19
