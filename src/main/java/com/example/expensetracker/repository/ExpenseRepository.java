package com.example.expensetracker.repository;

import com.example.expensetracker.dto.CategorySummary;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
    List<Expense> findByUserAndCategoryIgnoreCase(User user, String category);
    Optional<Expense> findByIdAndUser(Long id, User user);

    // 1. Groups all expenses by category and sums each category for this user
    @Query("SELECT new com.example.expensetracker.dto.CategorySummary(e.category, SUM(e.amount)) " +
            "FROM Expense e WHERE e.user = :user GROUP BY e.category")
    List<CategorySummary> findTotalByCategoryForUser(@Param("user") User user);

    // 2. Calculates total spending within a start and end date (COALESCE returns 0 instead of null if empty)
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
            "WHERE e.user = :user AND e.date BETWEEN :startDate AND :endDate")
    BigDecimal findTotalBetweenDatesForUser(@Param("user") User user,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}
