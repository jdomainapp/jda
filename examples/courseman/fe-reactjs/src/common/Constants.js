import React from 'react'

const constants =  {
  host: "http://localhost:8080",
  structures: {
  }
};

 const courseModules = [
  {
    "endpoint": "type", "name": "Type",
  },
  {
    "endpoint": "id", "name": "ID"
  },
  {
    "endpoint": "code", "name": "Code"
  },
  {
    "endpoint": "name", "name": "Name"
  },
  {
    "endpoint": "semester", "name": "Semester"
  },
  {
    "endpoint": "cost", "name": "Cost"
  },
  {
    "endpoint": "rating", "name": "Rating"
  },
  {
    "endpoint": "description", "name": "Description"
  },
  {
    "endpoint": "credit", "name": "Credit"
  }
]


const enrolment = [
  {
    "endpoint": "id", "name": "ID"
  },
  {
    "endpoint": "student_id", "name": "Student ID"
  },
  {
    "endpoint": "course", "name": "Course ID"
  },
  {
    "endpoint": "internal_mark", "name": "Internal mark"
  },
  {
    "endpoint": "exam_mark", "name": "Exam mark"
  },
  {
    "endpoint": "final_grade", "name": "Final grade"
  },
  {
    "endpoint": "final_mark", "name": "Final mark"
  },
  {
    "endpoint": "date_range", "name": "Date range"
  }
]

const address = [
  {
    "endpoint": "id", "name": "ID"
  },
  {
    "endpoint": "name", "name": "Name"
  },
  {
    "endpoint": "student_id", "name": "student ID"
  }
]

const students = [
  {
    "endpoint": "id", "name": "ID"
  },
  {
    "endpoint": "name", "name": "Name"
  },
  {
    "endpoint": "gender", "name": "gender"
  },
  {
    "endpoint": "dob", "name": "DoB"
  },
  {
    "endpoint": "address_id", "name": "Address ID"
  },
  {
    "endpoint": "email", "name": "Email"
  },
  {
    "endpoint": "class_id", "name": "Class ID"
  },
  {
    "endpoint": "address", "name": "Address",
    "subItem": address
  },
  {
    "endpoint": "enrolment", "name": "Enrolment",
    "subItem": enrolment
  }
]

const studentClasses = [
  {
    "endpoint": "id", "name": "ID"
  },
  {
    "endpoint": "student_id", "name": "Student ID"
  },
  {
    "endpoint": "course", "name": "Course ID"
  },
  {
    "endpoint": "internal_mark", "name": "Internal mark"
  },
  {
    "endpoint": "exam_mark", "name": "Exam mark"
  },
  {
    "endpoint": "final_grade", "name": "Final grade"
  },
  {
    "endpoint": "final_mark", "name": "Final mark"
  },
  {
    "endpoint": "date_range", "name": "Date range"
  },
  {
    "endpoint": "students", "name": "Students",
    "subItem": students
  }
]

export default constants;
export {courseModules, enrolment, studentClasses, address, students};