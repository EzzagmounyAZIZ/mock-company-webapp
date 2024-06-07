package com.mockcompany.webapp.service;

import com.mockcompany.webapp.data.ProductItemRepository;
import com.mockcompany.webapp.model.ProductItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class SearchService {

    private final ProductItemRepository productItemRepository;

    @Autowired
    public SearchService(ProductItemRepository productItemRepository) {
        this.productItemRepository = productItemRepository;
    }

    public Collection<ProductItem> search(String query) {
        // Normalize the query for case-insensitive matching
        String normalizedQuery = query.toLowerCase();

        // Check if the query is an exact match
        boolean exactMatch = isExactMatch(query);

        // Perform the search
        Stream<ProductItem> filteredItems = StreamSupport.stream(productItemRepository.findAll().spliterator(), false)
                .filter(item -> {
                    // Normalize the item's name and description for case-insensitive matching
                    String normalizedItemName = item.getName().toLowerCase();
                    String normalizedItemDesc = item.getDescription().toLowerCase();

                    // Check if the item's name or description contains the query
                    boolean nameMatches = exactMatch ? normalizedItemName.equals(normalizedQuery) : normalizedItemName.contains(normalizedQuery);
                    boolean descMatches = exactMatch ? normalizedItemDesc.equals(normalizedQuery) : normalizedItemDesc.contains(normalizedQuery);

                    return nameMatches || descMatches;
                });

        // Collect the filtered items into a list
        return filteredItems.collect(Collectors.toList());
    }

    private boolean isExactMatch(String query) {
        return query.startsWith("\"") && query.endsWith("\"");
    }
}

