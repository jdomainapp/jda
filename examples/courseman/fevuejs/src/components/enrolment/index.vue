<table class="table table-bordered table-hover">
    <thead>
        <tr>
            <th>#</th>
            <th>Id</th>
            <th>Student Name</th>
            <th>Coursemodule</th>
            <th>Internalmark</th>
            <th>Exammark</th>
            <th>Finalgrade</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>

        <tr style="cursor: pointer;" :key="enrolment.id" v-for="(enrolment, index) in enrolments.content" v-if="parentID === ''">
            <td style="vertical-align: middle;">{{index + 1}}</td>
            <td style="vertical-align: middle;">{{enrolment.id}}</td>
            <td style="vertical-align: middle;">{{enrolment.student.name}}</td>
            <td style="vertical-align: middle;">{{enrolment.courseModule.name}}</td>
            <td style="vertical-align: middle;">{{enrolment.internalMark}}</td>
            <td style="vertical-align: middle;">{{enrolment.examMark}}</td>
            <td style="vertical-align: middle;">{{enrolment.finalGrade}}</td>
            <td style="vertical-align: middle; width: 200px;">
                <button class="btn btn-primary" v-on:click="emitData(enrolment)">
                    Edit
                </button>
                <a class="btn btn-danger" id="btnShowModal" data-target="#myModal" v-on:click="getEnrolmentId(enrolment.id)" data-toggle="modal">
                    Delete
                </a>
            </td>
        </tr>


        <!-- <tr style="cursor: pointer;" :key="enrolment.id" v-for="(enrolment, index) in enrolments.content" v-if="(enrolment.student.name === enrolmentIn) && (eDisplay === 2)"> -->
        <tr style="cursor: pointer;" :key="enrolment.id" v-for="(enrolment, index) in enrolments.content" v-if="(enrolment.student.id === parentID) && (parentID != undefined)">
            <td style="vertical-align: middle;">{{index + 1}}</td>
            <td style="vertical-align: middle;">{{enrolment.id}}</td>
            <td style="vertical-align: middle;">{{enrolment.student.name}}</td>
            <td style="vertical-align: middle;">{{enrolment.courseModule.name}}</td>
            <td style="vertical-align: middle;">{{enrolment.internalMark}}</td>
            <td style="vertical-align: middle;">{{enrolment.examMark}}</td>
            <td style="vertical-align: middle;">{{enrolment.finalGrade}}</td>
            <td style="vertical-align: middle; width: 200px;">
                <button class="btn btn-primary" v-on:click="emitData(enrolment)">
                    Edit
                </button>
                <a class="btn btn-danger" id="btnShowModal" data-target="#myModal" v-on:click="getEnrolmentId(enrolment.id)" data-toggle="modal">
                    Delete
                </a>
            </td>
        </tr>
    </tbody>
    <Toasts></Toasts>
    <modal-confirm @confirm="deleteEnrolment(enrolmentId)"></modal-confirm>
</table>
