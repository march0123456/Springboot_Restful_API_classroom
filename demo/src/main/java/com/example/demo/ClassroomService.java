package com.example.demo;

import com.example.demo.exceptionHandler.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ClassroomService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;



    @Autowired
    public ClassroomService(TeacherRepository teacherRepository,
                            StudentRepository studentRepository,
                            CourseRepository courseRepository) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }


    public List<Object[]> getAllStudent() {
        return studentRepository.findStudents();
    }

    public ResponseEntity<Student> getSingleStudent( long sid) {
        Optional<Student> studentOptional = studentRepository.findById(sid);
        if(studentOptional.isPresent()){
            return new ResponseEntity<>(studentOptional.get(), HttpStatus.OK);
        }else{
            throw new RecordNotFoundException("Invalid id:"+ sid);
        }
    }



    public ResponseEntity<Teacher> getSingleTeacher(long tid) {
        Optional<Teacher> teacherOptional = teacherRepository.findById(tid);
        if(teacherOptional.isPresent()){
            return new ResponseEntity<>(teacherOptional.get(), HttpStatus.OK);
        }else{
            throw new RecordNotFoundException("Invalid id:"+ tid);
        }
    }

    public List<Teacher> getAllTeacher() {
        return teacherRepository.findAll();
    }

    public void addNewTeacher(Teacher teacher) {
       Optional<Teacher> teacherOptional = teacherRepository
               .findTeacherByNameContaining(teacher.getName());
       if (teacherOptional.isPresent()){
           throw new NotFoundException("registered already");
       }
       teacherRepository.save(teacher);


    }

    public void addNewStudent(Student student) {
        Optional<Student> studentOptional = studentRepository
                .findStudentByNameContaining(student.getName());
        if (studentOptional.isPresent()){
            throw new IllegalStateException("registered already");
        }
        studentRepository.save(student);

    }

    public List<CourseDTO> getCourses() {

        List<CourseDTO> courseList = new ArrayList<>();
        List<Course> courses = courseRepository.findAll();
        courses.stream().forEach(course ->{

            CourseDTO courseDTO = courseDTOEntity(course);
            courseList.add(courseDTO);

        } );

        return courseList  ;

    }
    private CourseDTO courseDTOEntity(Course course) {
        CourseDTO responseDTO = new CourseDTO();

        responseDTO.setSubject(course.getSubject());
        responseDTO.setCid(course.getCid());
        responseDTO.setMaximum_Student(course.getMaximum_Student());
        responseDTO.setSchedule_Time(course.getSchedule_Time());

        responseDTO.setStudents(course.getStudents().stream()
                .map(Student::getName).collect(Collectors.toSet()));
        return responseDTO;
    }


    public StudentDTO addStudentCourse(Long sid, StudentDTO studentDTO) {
        Student student = studentRepository.findById(sid).get();

        mapDtoToEntity(studentDTO, student);
        Student studentSaved = studentRepository.save(student);
        return mapDtoToEntity(studentSaved);
    }

    private void mapDtoToEntity(StudentDTO studentDto, Student student) {

        if(null == student.getCourses()){
            student.setCourses(new HashSet<>());
        }
        studentDto.getCourses().stream().forEach(courseName->{
            Course course = courseRepository.findBySubject(courseName);
            if (null == course){
                course = new Course();
                course.setStudents(new HashSet<>());
            }
            course.setSubject(courseName);
            student.addCourse(course);
        });
    }
    private StudentDTO mapDtoToEntity(Student student) {
        StudentDTO responseDTO = new StudentDTO();
        responseDTO.setName(student.getName());
        responseDTO.setId(student.getSid());
        responseDTO.setCourses(student.getCourses().stream()
                .map(Course::getSubject).collect(Collectors.toSet()));
        return  responseDTO;
    }


    public ResponseEntity<Student> deleteStudentCourse(Long sid, String course) {

        Optional<Student> student = studentRepository.findById(sid) ;
        if (!student.isPresent()){
            throw new NotFoundException("Id not found");
        }
        student.get().getCourses().stream().filter(course1
                -> course.equals(course1.getSubject())).map(Course::getSubject)
                .collect(Collectors.toSet());
//        for (Course courses : student.get().getCourses()){
//            if (course.equals(courses.getSubject())){
//            }
//        }
        student.get().removeCourse(courseRepository.findBySubject(course));
        studentRepository.save(student.get());

        return ResponseEntity.noContent().build();
    }
}
