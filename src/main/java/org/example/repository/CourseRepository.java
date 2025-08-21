package org.example.repository;

import org.example.dto.Course;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CourseRepository {
    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "Course";

    public CourseRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void save(Course course) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().n(String.valueOf(course.getId())).build());
        item.put("name", AttributeValue.builder().s(course.getName()).build());
        item.put("price", AttributeValue.builder().n(String.valueOf(course.getPrice())).build());
        item.put("description", AttributeValue.builder().s(course.getDescription()).build());
        item.put("authorName", AttributeValue.builder().s(course.getAuthorName()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public List<Course> findAll() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();

        ScanResponse response = dynamoDbClient.scan(scanRequest);

        return response.items().stream()
                .map(this::mapToCourse)
                .collect(Collectors.toList());
    }

    public Optional<Course> findById(int id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder().n(String.valueOf(id)).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();
        if (item == null || item.isEmpty()) return Optional.empty();

        return Optional.of(mapToCourse(item));
    }

    public boolean update(int id, Course newCourse) {
        if (!findById(id).isPresent()) return false;
        save(newCourse);
        return true;
    }

    public boolean delete(int id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder().n(String.valueOf(id)).build());

        DeleteItemRequest request = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

        DeleteItemResponse response = dynamoDbClient.deleteItem(request);
        return response.sdkHttpResponse().isSuccessful();
    }

    private Course mapToCourse(Map<String, AttributeValue> item) {
        return new Course(
                Integer.parseInt(item.get("id").n()),
                item.get("name").s(),
                Double.parseDouble(item.get("price").n()),
                item.get("description").s(),
                item.get("authorName").s()
        );
    }
}