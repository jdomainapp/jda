
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

export default students