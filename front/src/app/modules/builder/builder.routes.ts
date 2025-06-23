import { Routes } from "@angular/router";
import { BuilderComponent } from "./builder-screen/builder.component";
import { BuilderFormComponent } from "./builder-form/builder-form.component";

export default [
    {
        path     : ':name/:idScreen',
        children: [
            {
                path: '',
                component: BuilderComponent
            },
            {
                path: 'add',
                component: BuilderFormComponent,
            },
            {
                path: 'edit',
                component: BuilderFormComponent,
            }
        ]
    },
] as Routes;