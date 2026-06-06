/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : 127.0.0.1:3306
 Source Schema         : myfavs_master

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 12/02/2022 23:17:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_tenant
-- ----------------------------
DROP TABLE IF EXISTS `tb_tenant`;
CREATE TABLE `tb_tenant`  (
                           `id` bigint NOT NULL,
                           `created` datetime NULL DEFAULT NULL,
                           `modified` datetime NULL DEFAULT NULL,
                           `tenant` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                           `jdbc_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                           `jdbc_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                           `jdbc_password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                           `jdbc_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_tenant
-- ----------------------------
INSERT INTO `tb_tenant` VALUES (1492150022916149249, '2022-02-12 21:29:20', '2022-02-12 21:29:22', 'myfavs_master', 'jdbc:mysql://127.0.0.1:3306/myfavs_master?allowPublicKeyRetrieval=true&useUnicode=true&useServerPrepStmts=false&rewriteBatchedStatements=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8', 'root', 'root', 'com.mysql.cj.jdbc.Driver');
INSERT INTO `tb_tenant` VALUES (1492150022916149250, '2022-02-12 21:29:20', '2022-02-12 21:29:22', 'myfavs_tenant1', 'jdbc:mysql://127.0.0.1:3306/myfavs_tenant1?allowPublicKeyRetrieval=true&useUnicode=true&useServerPrepStmts=false&rewriteBatchedStatements=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8', 'root', 'root', 'com.mysql.cj.jdbc.Driver');
INSERT INTO `tb_tenant` VALUES (1492150022916149251, '2022-02-12 21:29:20', '2022-02-12 21:29:22', 'myfavs_tenant2', 'jdbc:mysql://127.0.0.1:3306/myfavs_tenant2?allowPublicKeyRetrieval=true&useUnicode=true&useServerPrepStmts=false&rewriteBatchedStatements=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8', 'root', 'root', 'com.mysql.cj.jdbc.Driver');

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
                         `id` bigint NOT NULL,
                         `created` datetime NULL DEFAULT NULL,
                         `modified` datetime NULL DEFAULT NULL,
                         `username` nvarchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                         `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                         `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                         `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES (1492150022916149248, '2022-02-11 12:00:00', '2022-02-11 12:00:01', 'master_user1', 'master_user1@myfavs_master.com', '123456', 'ADMIN');

SET FOREIGN_KEY_CHECKS = 1;
