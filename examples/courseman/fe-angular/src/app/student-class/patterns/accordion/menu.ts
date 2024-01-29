export const MenuStudentClass = [
    {
        "endpoint": "id", "name": "ID"
    },
    {
        "endpoint": "name", "name": "Name"
    },
    {
        "endpoint": "student", "name": "Student",
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
                "endpoint": "address-id", "name": "Address ID",
                "subItem": [
                    {
                        "endpoint": "id", "name": "ID"
                    },
                    {
                        "endpoint": "name", "name": "City name"
                    },
                ]
            },
            {
                "endpoint": "enrolment", "name": "Enrolment",
                "subItem": [
                    {
                        "endpoint": "id", "name": "ID"
                    },
                    {
                        "endpoint": "name", "name": "Course Module ID"
                    },
                    {
                        "endpoint": "internal-mark", "name": "Internal Mark"
                    },
                    {
                        "endpoint": "exam-mark", "name": "Exam Mark",
                    },
                    {
                        "endpoint": "start-date-end-date", "name": "Start date - End date",
                    },
                ]
            },
        ]
    },
   
];