package com.cibertec.assessment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.assessment.model.Square;
import com.cibertec.assessment.service.SquareService;

@RestController
@RequestMapping("/squares")
public class SquareController {

    @Autowired
    private SquareService squareService;

    @GetMapping
    public ResponseEntity<List<Square>> getAllSquares() {
        List<Square> squares = squareService.list();
        return new ResponseEntity<>(squares, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Square> createSquare(@RequestBody Square square) {
        Square createdSquare = squareService.create(square);
        return new ResponseEntity<>(createdSquare, HttpStatus.CREATED);
        
    }
    
    @GetMapping("/squaresByname")
    public ResponseEntity<List<Square>> getSquaresByName(@RequestParam String name) {
        List<Square> squares = squareService.findSquaresByName(name);
        return ResponseEntity.ok(squares);
    }

}
