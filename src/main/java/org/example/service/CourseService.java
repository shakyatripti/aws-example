package org.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.dto.Course;
import org.example.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseService {
    /*private final List<Course> courses = new ArrayList<>();

    // Create a new course
    public void addCourse(Course course) {
        courses.add(course);
    }

    // Retrieve all courses
    public List<Course> getAllCourses() {
        return courses;
    }

    // Retrieve a course by id
    public Optional<Course> getCourseById(int id) {
        return courses.stream().filter(course -> course.getId() == id).findFirst();
    }

    // Update a course
    public boolean updateCourse(int id, Course newCourse) {
        return getCourseById(id).map(existingCourse -> {
            courses.remove(existingCourse);
            courses.add(newCourse);
            return true;
        }).orElse(false);
    }

    // Delete a course by id
    public boolean deleteCourse(int id) {
        return courses
                .removeIf(course -> course.getId() == id);
    }*/

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public void addCourse(Course course) {
        courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(int id) {
        return courseRepository.findById(id);
    }

    public boolean updateCourse(int id, Course newCourse) {
        return courseRepository.update(id, newCourse);
    }

    public boolean deleteCourse(int id) {
        return courseRepository.delete(id);
    }
}
