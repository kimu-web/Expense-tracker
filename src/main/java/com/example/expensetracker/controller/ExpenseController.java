package com.example.expensetracker.controller;

import com.example.expensetracker.dto.CategorySummary;
import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @GetMapping
    public List<Expense> getExpenses(@RequestParam(required = false) String category, Principal principal) {
        return service.getAllForUser(principal.getName(), category);
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id, Principal principal) {
        return service.getByIdForUser(id, principal.getName());
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody ExpenseRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createForUser(request, principal.getName()));
    }

    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request, Principal principal) {
        return service.updateForUser(id, request, principal.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, Principal principal) {
        service.deleteForUser(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics/by-category")
    public List<CategorySummary> getSpendingByCategory(Principal principal) {
        return service.getCategorySummaryForUser(principal.getName());
    }

    @GetMapping("/analytics/total")
    public Map<String, Object> getTotalSpending(@RequestParam LocalDate startDate,
                                                @RequestParam LocalDate endDate,
                                                Principal principal) {
        return service.getTotalBetweenDatesForUser(principal.getName(), startDate, endDate);
    }
}
