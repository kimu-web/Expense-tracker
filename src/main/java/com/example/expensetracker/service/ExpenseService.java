package com.example.expensetracker.service;

import com.example.expensetracker.dto.CategorySummary;
import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    public List<Expense> getAllForUser(String username, String category) {
        User user = getUserByUsername(username);
        if (category != null && !category.isBlank()) {
            return expenseRepository.findByUserAndCategoryIgnoreCase(user, category);
        }
        return expenseRepository.findByUser(user);
    }

    public Expense getByIdForUser(Long id, String username) {
        User user = getUserByUsername(username);
        return expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + id));
    }

    public Expense createForUser(ExpenseRequest request, String username) {
        User user = getUserByUsername(username);
        Expense expense = new Expense(
                request.description(),
                request.amount(),
                request.category(),
                request.date(),
                user
        );
        return expenseRepository.save(expense);
    }

    public Expense updateForUser(Long id, ExpenseRequest request, String username) {
        Expense existing = getByIdForUser(id, username);
        existing.setDescription(request.description());
        existing.setAmount(request.amount());
        existing.setCategory(request.category());
        existing.setDate(request.date());
        return expenseRepository.save(existing);
    }

    public void deleteForUser(Long id, String username) {
        Expense existing = getByIdForUser(id, username);
        expenseRepository.delete(existing);
    }

    public List<CategorySummary> getCategorySummaryForUser(String username) {
        User user = getUserByUsername(username);
        return expenseRepository.findTotalByCategoryForUser(user);
    }

    public Map<String, Object> getTotalBetweenDatesForUser(String username, LocalDate startDate, LocalDate endDate) {
        User user = getUserByUsername(username);
        BigDecimal total = expenseRepository.findTotalBetweenDatesForUser(user, startDate, endDate);
        return Map.of(
                "startDate", startDate,
                "endDate", endDate,
                "totalSpent", total
        );
    }
}

