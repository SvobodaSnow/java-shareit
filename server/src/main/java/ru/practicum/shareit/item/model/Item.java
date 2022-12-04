package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "items", schema = "public")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 64)
    private String name;
    @Column(nullable = false, length = 512)
    private String description;
    @Column(nullable = false)
    private Boolean available;
    @Column(nullable = false)
    private Long owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
