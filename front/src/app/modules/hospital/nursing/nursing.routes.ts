import { Routes } from "@angular/router";
import { AttendenceComponent } from "./attendence/attendence.component";

export default [
    {
        path     : '',
        children: [
            {
                path: '',
                component: AttendenceComponent
            },
        ]
    },
] as Routes;