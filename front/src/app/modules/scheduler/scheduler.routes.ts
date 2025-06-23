import { Routes } from "@angular/router";
import { SchedulerAtendenceComponent } from "./scheduler-atendence/scheduler-atendence.component";

export default [
	{
		path: '',
		children: [
			{
				path: '',
				component: SchedulerAtendenceComponent,
			}
		]
	}
] as Routes;