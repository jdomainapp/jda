// This file is to store global variables
import Vue from "vue";

// display can be number or null
export const store = Vue.observable({
    // demo nested tree
    form_trees: [
        {
            name: "StudentClass",
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
                                                {
                                                    name: "City name",
                                                    children: [],
                                                },
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
        {
            name: "Student",
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
                    ],
                },
            ],
        },
    ],

    // can be either null or recursive tree
    tree: {
        name: "root",
        children: [],
    },
});

export const mutations = {
    // demo nested tree
    addNode(parentNode, sidebarID) {
        let newNode = {
            name: sidebarID,
            children: [],
        };
        parentNode.children.push(newNode);
    },
};
