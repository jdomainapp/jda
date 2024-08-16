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
    "endpoint": "student_id", "name": "Student ID"
  },
  {
    "endpoint": "students", "name": "Students",
    "subItem": [
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
        "endpoint": "email", "name": "Email"
      },
      {
        "endpoint": "class_id", "name": "Class ID"
      },
      {
        "endpoint": "enrolment", "name": "Enrolment",
        "subItem": [
          {
            "endpoint": "id", "name": "ID"
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
      }
    ]
  },
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
    "endpoint": "address", "name": "Address",
    "subItem": [
      {
        "endpoint": "id", "name": "ID"
      },
      {
        "endpoint": "name", "name": "Name"
      }
    ]
  },
  {
    "endpoint": "email", "name": "Email"
  },
  {
    "endpoint": "class_id", "name": "Class ID"
  },
  {
    "endpoint": "enrolment", "name": "Enrolment",
    "subItem": [
      {
        "endpoint": "id", "name": "ID"
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
  }
]

const studentClasses = [
  {
    "endpoint": "id", "name": "ID"
  },
  {
    "endpoint": "name", "name": "Name"
  },
  {
    "endpoint": "students", "name": "Students",
    "subItem": [
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
        "endpoint": "address", "name": "Address",
        "subItem": [
          {
            "endpoint": "id", "name": "ID"
          },
          {
            "endpoint": "name", "name": "Name"
          }
        ]
      },
      {
        "endpoint": "email", "name": "Email"
      },
      {
        "endpoint": "enrolment", "name": "Enrolment",
        "subItem": [
          {
            "endpoint": "id", "name": "ID"
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
      }
    ]
  }
]

export default constants;
export {courseModules, enrolment, studentClasses, address, students};