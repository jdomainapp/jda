const constants =  {
  host: "http://localhost:8080",
  structures: {
  }
};

 const courseModules = [
  {
    "endpoint": "#", "name": "Fields",
    "subItem": [
      {
        "endpoint": "#type", "name": "Type",
      },
      {
        "endpoint": "#id", "name": "ID"
      },
      {
        "endpoint": "#code", "name": "Code"
      },
      {
        "endpoint": "#name", "name": "Name"
      },
      {
        "endpoint": "#semester", "name": "Semester"
      },
      {
        "endpoint": "#cost", "name": "Cost"
      },
      {
        "endpoint": "#rating", "name": "Rating"
      },
      {
        "endpoint": "#description", "name": "Description"
      },
      {
        "endpoint": "#credit", "name": "Credit"
      }
    ]
  }
]


const enrolments = [
  {
    "endpoint": "#", "name": "Fields",
    "subItem": [
      {
        "endpoint": "#id", "name": "ID"
      },
      {
        "endpoint": "#student-id", "name": "Student ID"
      },
      {
        "endpoint": "#course", "name": "Course ID"
      },
      {
        "endpoint": "#internal-mark", "name": "Internal mark"
      },
      {
        "endpoint": "#exam-mark", "name": "Exam mark"
      },
      {
        "endpoint": "#final-grade", "name": "Final grade"
      },
      {
        "endpoint": "#final-mark", "name": "Final mark"
      },
      {
        "endpoint": "#date-range", "name": "Date range"
      }
    ]
  }
]


const studentClasses = [
  {
    "endpoint": "#", "name": "Fields",
    "subItem": [
      {
        "endpoint": "#id", "name": "ID"
      },
      {
        "endpoint": "#student-id", "name": "Student ID"
      },
      {
        "endpoint": "#course", "name": "Course ID"
      },
      {
        "endpoint": "#internal-mark", "name": "Internal mark"
      },
      {
        "endpoint": "#exam-mark", "name": "Exam mark"
      },
      {
        "endpoint": "#final-grade", "name": "Final grade"
      },
      {
        "endpoint": "#final-mark", "name": "Final mark"
      },
      {
        "endpoint": "#date-range", "name": "Date range"
      }
    ]
  }
]


const address = [
  {
    "endpoint": "#", "name": "Fields",
    "subItem": [
      {
        "endpoint": "#id", "name": "ID"
      },
      {
        "endpoint": "#name", "name": "Name"
      },
      {
        "endpoint": "#student-id", "name": "student ID"
      },
    ]
  }
]

const students = [
  {
    "endpoint": "#", "name": "Fields",
    "subItem": [
      {
        "endpoint": "#id", "name": "ID"
      },
      {
        "endpoint": "#name", "name": "Name"
      },
      {
        "endpoint": "#gender", "name": "gender"
      },
      {
        "endpoint": "#dob", "name": "DoB"
      },
      {
        "endpoint": "#address-id", "name": "Address ID"
      },
      {
        "endpoint": "#email", "name": "Email"
      },
      {
        "endpoint": "#class-id", "name": "Class ID"
      }
    ]
  },
  {
    "endpoint": "#", "name": "Sub-modules",
    "subItem": [
      {
        "endpoint": "#address", "name": "Address",
        "subItem": address
      },
      {
        "endpoint": "#enrolment", "name": "Enrolment",
        "subItem": enrolments
      }
    ]
  }
]

export default constants;
export {courseModules, enrolments, studentClasses, address, students};