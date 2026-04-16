package com.SCM.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * This is an automatic Database Migration Tool.
 * It will run exactly once every time you start your Spring Boot application.
 * It scans all 51 of your tables and converts any legacy `NULL` values in the 
 * `deleted` column to `false` (0).
 * 
 * This INSTANTLY fixes the UI issue where old data was not showing up,
 * without needing to manually rewrite 50+ Entity and Repository Java files!
 */
//@Component
//public class DatabaseNullFixer implements CommandLineRunner {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // List of all your 51 tables
//        String[] tables = {
//            "activity", "activity_program", "activity_program_instance", "business",
//            "country", "customer", "customer_membership", "customer_relation",
//            "deposit", "employee", "faq", "faq_list", "invoice", "lead_activity",
//            "lead_contact", "lead_main", "membership", "membership_tier", "merchant",
//            "online_store", "permission", "photo", "piggy_bank", "promotion_content",
//            "promotion", "promotion_instance", "promotion_instance_batch",
//            "promotion_trace", "raffle_pot", "raffle_prize", "raffle_prize_group",
//            "raffle_run", "raffle_ticket", "redeem", "redemption_rule", "referral",
//            "region", "report", "reward", "reward_code", "reward_code_group",
//            "reward_trace", "role", "role_permission", "tag", "tds", "transaction",
//            "user", "user_membership", "user_verification", "withdraw"
//        };
//
//        System.out.println("\n========================================================");
//        System.out.println("🚀 RUNNING AUTOMATIC LEGACY DATA FIXER 🚀");
//        System.out.println("Fixing NULL 'deleted' flags so old data shows in UI...");
//
//        int totalFixed = 0;
//
//        for (String table : tables) {
//            try {
//                // Safely update NULL to 0 (false) for older records
//                int updated = jdbcTemplate.update("UPDATE " + table + " SET deleted = 0 WHERE deleted IS NULL");
//                if (updated > 0) {
//                    System.out.println("✅ Fixed " + updated + " hidden records in table: " + table);
//                    totalFixed += updated;
//                }
//            } catch (Exception e) {
//                // Safely ignore if the table doesn't have a deleted column
//            }
//        }
//
//        System.out.println("🎉 TOTAL LEGACY RECORDS RESTORED: " + totalFixed);
//        System.out.println("========================================================\n");
//    }
//}