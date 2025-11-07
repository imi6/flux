-- MySQL dump 10.13  Distrib 5.7.44, for Linux (x86_64)
--
-- Host: localhost    Database: fluxgost
-- ------------------------------------------------------
-- Server version	5.7.44-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `forward`
--

DROP TABLE IF EXISTS `forward`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forward` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL,
  `user_name` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `tunnel_id` int(10) NOT NULL,
  `in_port` int(10) NOT NULL,
  `out_port` int(10) DEFAULT NULL,
  `remote_addr` longtext NOT NULL,
  `strategy` varchar(100) NOT NULL DEFAULT 'fifo',
  `interface_name` varchar(200) DEFAULT NULL,
  `in_flow` bigint(20) NOT NULL DEFAULT '0',
  `out_flow` bigint(20) NOT NULL DEFAULT '0',
  `created_time` bigint(20) NOT NULL,
  `updated_time` bigint(20) NOT NULL,
  `status` int(10) NOT NULL,
  `inx` int(10) NOT NULL DEFAULT '0',
  `hop_nodes_config` text COMMENT 'ä¸­è½¬èŠ‚ç‚¹é…ç½®ï¼ˆä»…ç”¨äºŽå¤šçº§éš§é“è½¬å‘ï¼‰',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forward`
--

LOCK TABLES `forward` WRITE;
/*!40000 ALTER TABLE `forward` DISABLE KEYS */;
/*!40000 ALTER TABLE `forward` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `node`
--

DROP TABLE IF EXISTS `node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `node` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `secret` varchar(100) NOT NULL,
  `ip` longtext,
  `server_ip` varchar(100) NOT NULL,
  `port_sta` int(10) NOT NULL,
  `port_end` int(10) NOT NULL,
  `version` varchar(100) DEFAULT NULL,
  `created_time` bigint(20) NOT NULL,
  `updated_time` bigint(20) DEFAULT NULL,
  `status` int(10) NOT NULL,
  `http` int(10) DEFAULT '0' COMMENT 'HTTP æœåŠ¡ç«¯å£',
  `tls` int(10) DEFAULT '0' COMMENT 'TLS æœåŠ¡ç«¯å£',
  `socks` int(10) DEFAULT '0' COMMENT 'SOCKS æœåŠ¡ç«¯å£',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `node`
--

LOCK TABLES `node` WRITE;
/*!40000 ALTER TABLE `node` DISABLE KEYS */;
/*!40000 ALTER TABLE `node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `speed_limit`
--

DROP TABLE IF EXISTS `speed_limit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `speed_limit` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `speed` int(10) NOT NULL,
  `tunnel_id` int(10) NOT NULL,
  `tunnel_name` varchar(100) NOT NULL,
  `created_time` bigint(20) NOT NULL,
  `updated_time` bigint(20) DEFAULT NULL,
  `status` int(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `speed_limit`
--

LOCK TABLES `speed_limit` WRITE;
/*!40000 ALTER TABLE `speed_limit` DISABLE KEYS */;
/*!40000 ALTER TABLE `speed_limit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `statistics_flow`
--

DROP TABLE IF EXISTS `statistics_flow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `statistics_flow` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL,
  `flow` bigint(20) NOT NULL,
  `total_flow` bigint(20) NOT NULL,
  `time` varchar(100) NOT NULL,
  `created_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=748 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statistics_flow`
--

LOCK TABLES `statistics_flow` WRITE;
/*!40000 ALTER TABLE `statistics_flow` DISABLE KEYS */;
/*!40000 ALTER TABLE `statistics_flow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tunnel`
--

DROP TABLE IF EXISTS `tunnel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tunnel` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `traffic_ratio` decimal(10,1) NOT NULL DEFAULT '1.0',
  `in_node_id` int(10) NOT NULL,
  `in_ip` varchar(100) NOT NULL,
  `out_node_id` int(10) NOT NULL,
  `out_ip` varchar(100) NOT NULL,
  `type` int(10) NOT NULL COMMENT '1-ç«¯å£è½¬å‘, 2-éš§é“è½¬å‘, 3-ç«¯å£å¤ç”¨, 4-å¤šçº§éš§é“è½¬å‘',
  `protocol` varchar(10) NOT NULL DEFAULT 'tls',
  `flow` int(10) NOT NULL,
  `tcp_listen_addr` varchar(100) NOT NULL DEFAULT '[::]',
  `udp_listen_addr` varchar(100) NOT NULL DEFAULT '[::]',
  `interface_name` varchar(200) DEFAULT NULL,
  `ss_config` text COMMENT 'SSèŠ‚ç‚¹é…ç½®(ç«¯å£å¤ç”¨æ—¶ä½¿ç”¨)',
  `hop_nodes` text COMMENT 'å¤šçº§èŠ‚ç‚¹é…ç½®(JSONæ ¼å¼)ï¼Œå­˜å‚¨èŠ‚ç‚¹é“¾è·¯ä¿¡æ¯',
  `created_time` bigint(20) NOT NULL,
  `updated_time` bigint(20) NOT NULL,
  `status` int(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tunnel`
--

LOCK TABLES `tunnel` WRITE;
/*!40000 ALTER TABLE `tunnel` DISABLE KEYS */;
/*!40000 ALTER TABLE `tunnel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user` varchar(100) NOT NULL,
  `pwd` varchar(100) NOT NULL,
  `role_id` int(10) NOT NULL,
  `exp_time` bigint(20) NOT NULL,
  `flow` bigint(20) NOT NULL,
  `in_flow` bigint(20) NOT NULL DEFAULT '0',
  `out_flow` bigint(20) NOT NULL DEFAULT '0',
  `flow_reset_time` bigint(20) NOT NULL,
  `num` int(10) NOT NULL,
  `created_time` bigint(20) NOT NULL,
  `updated_time` bigint(20) DEFAULT NULL,
  `status` int(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','272cf838ce43c31d94e2a051c820fce5',0,2727251700000,99999,3217759214,46220582995,1,99999,1748914865000,1759822036736,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_tunnel`
--

DROP TABLE IF EXISTS `user_tunnel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_tunnel` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL,
  `tunnel_id` int(10) NOT NULL,
  `speed_id` int(10) DEFAULT NULL,
  `num` int(10) NOT NULL,
  `flow` bigint(20) NOT NULL,
  `in_flow` bigint(20) NOT NULL DEFAULT '0',
  `out_flow` bigint(20) NOT NULL DEFAULT '0',
  `flow_reset_time` bigint(20) NOT NULL,
  `exp_time` bigint(20) NOT NULL,
  `status` int(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_tunnel`
--

LOCK TABLES `user_tunnel` WRITE;
/*!40000 ALTER TABLE `user_tunnel` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_tunnel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vite_config`
--

DROP TABLE IF EXISTS `vite_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vite_config` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `value` varchar(200) NOT NULL,
  `time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vite_config`
--

LOCK TABLES `vite_config` WRITE;
/*!40000 ALTER TABLE `vite_config` DISABLE KEYS */;
INSERT INTO `vite_config` VALUES (1,'app_name','自用转发',1760399525249),(2,'ip','1.1.1.1:16365',1760399525253);
/*!40000 ALTER TABLE `vite_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'fluxgost'
--

--
-- Dumping routines for database 'fluxgost'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-07 19:01:16
