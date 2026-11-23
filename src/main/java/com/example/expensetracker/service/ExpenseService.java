package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public List<Expense> getAll(String category) {
        if (category != null && !category.isBlank()) {
            return repository.findByCategoryIgnoreCase(category);
        }
        return repository.findAll();
    }

    public Expense getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + id));
    }

    public Expense create(ExpenseRequest request) {
        Expense expense = new Expense(
                request.description(),
                request.amount(),
                request.category(),
                request.date()
        );
        return repository.save(expense);
    }

    public Expense update(Long id, ExpenseRequest request) {
        Expense existing = getById(id);
        existing.setDescription(request.description());
        existing.setAmount(request.amount());
        existing.setCategory(request.category());
        existing.setDate(request.date());
        return repository.save(existing);
    }

    public void delete(Long id) {
        Expense existing = getById(id);
        repository.delete(existing);
    }
}
