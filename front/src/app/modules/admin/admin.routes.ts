import { Routes } from '@angular/router';
import { PainelComponent } from './painel/painel.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { inject } from '@angular/core';
import { AddComponent } from './sidebar/add/add.component';
import { AddPanelComponent } from './painel/add/add.component';
import { NavigationService } from 'app/core/navigation/navigation.service';
import { ScreenComponent } from './screen/screen.component';
import { DefaultApi } from 'app/app-api/default/api';
import { AddScreenComponent } from './screen/add/add.component';
import { AddFieldComponent } from './screen/builder/add-field/add-field.component';
import { AddDivComponent } from './screen/builder/add-div/add-div.component';
import { GroupComponent } from './group/group.component';
import { AddGroupComponent } from './group/add-group/add-group.component';

export default [
	{
		path: '',
		children: [
			{
				path: '',
				component: PainelComponent,
				resolve: {
					panels: () => inject(DefaultApi).getAllItems('panel/'),
					nav: () => inject(NavigationService).get(),
				},
			},
			{
				path: 'add',
				component: AddPanelComponent,
			},
			{
				path: 'edit/:id',
				component: AddPanelComponent,
			},
		],
	},
	{
		path: 'sidebar',
		children: [
			{
				path: '',
				component: SidebarComponent,
				resolve: {
					sides: () => inject(DefaultApi).getAllItems('sidebar/'),
				},
			},
			{
				path: 'add',
				component: AddComponent,
			},
			{
				path: 'edit/:id',
				component: AddComponent,
			},
		],
	},
	{
		path: 'group',
		children: [
			{
				path: '',
				component: GroupComponent,
				resolve: {
					sides: () => inject(DefaultApi).getAllItems('menu/group/'),
				},
			},
			{
				path: 'add',
				component: AddGroupComponent,
			},
			{
				path: 'edit/:id',
				component: AddGroupComponent,
			},
		],
	},
	{
		path: 'screens',
		children: [
			{
				path: '',
				component: ScreenComponent,
				resolve: {
					sides: () =>
						inject(DefaultApi).getAllItems('builder/screen/'),
				}
			},
			{
				path: 'add',
				component: AddScreenComponent,
			},
			{
				path: 'edit/:id',
				children: [
					{
						path: '',
						component: AddScreenComponent
					},
					{
						path: 'field',
						children: [
							{
								path: '',
								component: AddFieldComponent
							},
							{
								path: ':idField',
								component: AddFieldComponent
							}
						]
					},
					{
						path: 'div',
						children: [
							{
								path: '',
								component: AddDivComponent
							},
							{
								path: ':idDiv',
								component: AddDivComponent
							}
						]
					}
				]
			},
		],
	},
	/**
    {
        path: 'new',
        component: EditComponent,
        resolve  : {
            boards: () => inject(ScrumboardService).getBoards(),
        },
    }
     */
] as Routes;
