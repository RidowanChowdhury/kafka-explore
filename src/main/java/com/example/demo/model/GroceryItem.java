package com.example.demo.model;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GroceryItem {

    private String id;

    private String name;
    private int quantity;
    private String category;
}
