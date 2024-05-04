export default class CourseModule {
    constructor(
        type,
        id,
        code,
        name,
        semester,
        credits,
        description,
        deptName,
        rating,
        cost
    ) {
        this.type = type;
        this.id = id;
        this.code = code;
        this.name = name;
        this.semester = semester;
        this.credits = credits;
        this.description = description;
        this.deptName = deptName;
        this.rating = rating;
        this.cost = cost;
    }
}
