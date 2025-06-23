import { Routes } from "@angular/router";
import { TherapeuticPlanningComponent } from "./therapeutic-planning/therapeutic-planning.component";

export default [
    {
        path     : '',
        children: [
            {
                path: 'therapeutic-planning',
                component: TherapeuticPlanningComponent
            },
        ]
    },
] as Routes;