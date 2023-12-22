// This file is to store variables that are used in the store
import Vue from "vue";

// display can be number or null
export const store = Vue.observable({
    display: null,

    // demo nested tree
    form_tree: {
        name: "Form: Student class",
        children: [
            { name: "Name", children: [] },
            {
                name: "Form: Student",
                children: [
                    { name: "Full Name", children: [] },
                    { name: "Date of Birth", children: [] },
                    { name: "Email", children: [] },
                    { name: "Gender", children: [] },
                    {
                        name: "Address ID",
                        children: [{ name: "City name", children: [] }],
                    },
                    {
                        name: "Student class ID",
                        children: [{ name: "Name", children: [] }],
                    },
                    {
                        name: "Form: Enrolment",
                        children: [
                            { name: "Internal Mark", children: [] },
                            { name: "Exam Mark", children: [] },
                            {
                                name: "Student ID",
                                children: [
                                    { name: "Full Name", children: [] },
                                    { name: "Date of Birth", children: [] },
                                    { name: "Email", children: [] },
                                    { name: "Gender", children: [] },
                                    {
                                        name: "Address ID",
                                        children: [
                                            { name: "City name", children: [] },
                                        ],
                                    },
                                    {
                                        name: "Student class ID",
                                        children: [
                                            { name: "Name", children: [] },
                                        ],
                                    },
                                ],
                            },
                            {
                                name: "Course module ID",
                                children: [
                                    { name: "Type", children: [] },
                                    { name: "Name", children: [] },
                                    { name: "Semester", children: [] },
                                    { name: "Credits", children: [] },
                                ],
                            },
                        ],
                    },
                ],
            },
        ],
    },
});

export const mutations = {
    setDisplay(value) {
        store.display = value;
    },
};
