package com.example.demo;

import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PreRemove;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/classroom")
public class ClassroomController {
    private final ClassroomService classroomService;
    private final TeacherRepository teacherRepository;

    private final CourseRepository courseRepository;
    @Autowired
    public ClassroomController(ClassroomService classroomService,
                               TeacherRepository teacherRepository,
                               CourseRepository courseRepository)
    { this.classroomService = classroomService;
        this.courseRepository = courseRepository;
    this.teacherRepository = teacherRepository;}



    //GetAllStudent
    @GetMapping(path = "/students")
    public List<Object[]> getAllStudent() {
        return  classroomService.getAllStudent();}


    //GetAllTeacher
    @GetMapping(path = "/teachers")
    public List<Teacher> getAllTeacher(){
        return  classroomService.getAllTeacher();
    }
    //GetSingleStudent
    @GetMapping("/student/{sid}")
    public ResponseEntity<Student> getSingleStudent(
            @PathVariable long sid){
        return classroomService.getSingleStudent(sid);

    }
    //GetSingleTeacher
    @GetMapping(path = "/teacher/{tid}")
    public ResponseEntity<Teacher> getSingleTeacher(@PathVariable long tid){
        return  classroomService.getSingleTeacher(tid);
    }
    //AddTeacher
    @PostMapping(path = "/teacher")
    public void addNewTeacher (@RequestBody Teacher teacher){
        classroomService.addNewTeacher(teacher);
    }
    //AddStudent
    @PostMapping(path ="/student")
    public void addNewStudent (@RequestBody Student student)
    {classroomService.addNewStudent(student);}

    //TeacherCreateCourse
    @PostMapping(path = "/course/{teacherId}")
    public Optional<Course> createCourse(@PathVariable (value = "teacherId")Long tid,
                                         @RequestBody Course course ){
        return teacherRepository.findById(tid).map(teacher -> {
            course.setTid(teacher);
//
            return courseRepository.save(course);}
        );
    }
    //TeacherDeleteCourse
    @DeleteMapping("/teacher/{teacherId}/course/{courseId}")
    public ResponseEntity<Object> deleteCourse(
            @PathVariable (value = "teacherId") Long tid
    ,@PathVariable(value = "courseId")Long cid){

        return courseRepository.findById(cid).map(course -> {
            courseRepository.delete(course);
            course.removeCourseFromStudent();
            return  ResponseEntity.ok().build();
        }).orElseThrow(()-> new NotFoundException("Course already doesn't exists"));
    }



    //TeacherUpdateCourse
    @PutMapping("/course/{courseId}")
    public Course updateCourse(@PathVariable(value = "courseId")Long cid,
                               @RequestBody Course courseUpdate){
        return courseRepository.findById(cid)
                .map(course -> {
                    course.setSubject(courseUpdate.getSubject());
                    course.setSchedule_Time(courseUpdate.getSchedule_Time());
                    course.setMaximum_Student(courseUpdate.getMaximum_Student());
                    return courseRepository.save(course);
                }).orElseThrow(()-> new NotFoundException("Course not found with id" + cid));
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseDTO>>getCourses(){
        List<CourseDTO> courses = classroomService.getCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @PostMapping("/stu/addStudent/{sid}")
    public ResponseEntity<StudentDTO> addStudentCourse(@PathVariable(name = "sid")Long sid,
                                                 @RequestBody StudentDTO studentDTO){
        StudentDTO std = classroomService.addStudentCourse(sid, studentDTO);
        return new ResponseEntity<>(std, HttpStatus.OK);
    }
    @DeleteMapping("student/{sid}/course/{course}")
    public ResponseEntity<Student> deleteStudentCourse(
            @PathVariable(value = "sid")Long sid,
            @PathVariable(value = "course")String course
    ){
        return classroomService.deleteStudentCourse(sid, course);
    }
}
