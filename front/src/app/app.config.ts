import { HTTP_INTERCEPTORS, provideHttpClient } from '@angular/common/http';
import { APP_INITIALIZER, ApplicationConfig, importProvidersFrom, inject } from '@angular/core';
import { LuxonDateAdapter } from '@angular/material-luxon-adapter';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { provideAnimations } from '@angular/platform-browser/animations';
import { PreloadAllModules, provideRouter, withHashLocation, withInMemoryScrolling, withPreloading } from '@angular/router';
import { provideFuse } from '@fuse';
import { provideTransloco, TranslocoService } from '@ngneat/transloco';
import { appRoutes } from 'app/app.routes';
import { provideAuth } from 'app/core/auth/auth.provider';
import { provideIcons } from 'app/core/icons/icons.provider';
import { mockApiServices } from 'app/mock-api';
import { provideQuillConfig } from 'ngx-quill';
import { firstValueFrom } from 'rxjs';
import { TranslocoHttpLoader } from './core/transloco/transloco.http-loader';

import { registerLocaleData } from '@angular/common';
import Counter from './counter';

import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarModule, DateAdapter as t } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { provideToastr } from 'ngx-toastr';

// location
import localePt from '@angular/common/locales/pt';
import localePtExtra from '@angular/common/locales/extra/pt';
import { HttpCoreInterceptor } from './http-core.interceptor';

registerLocaleData(localePt, 'pt-BR', localePtExtra);

export const appConfig: ApplicationConfig = {
    providers: [
        provideAnimations(),
        provideToastr({
            timeOut: 5000,
            preventDuplicates: true,
            iconClasses: {
                error: 'toast-error',
                info: 'toast-info',
                success: 'toast-success',
                warning: 'toast-warning',
            }
        }),
        {provide: MAT_DATE_LOCALE, useValue: 'pt-br'},
        { provide: HTTP_INTERCEPTORS, useClass: HttpCoreInterceptor, multi: true },
        {provide: 'LOCALE_ID', useValue: 'pt-BR'},
        {provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: { duration: 2500 }},
        importProvidersFrom(
            CalendarModule.forRoot({
              provide: t,
              useFactory: adapterFactory,
            })
        ),
        FullCalendarModule,
        provideQuillConfig({
            customModules: [{
                implementation: Counter,
                path: 'modules/counter'
            }],
            modules: [{
                toolbar: [
                    ['bold', 'italic', 'underline', 'strike'],        // toggled buttons
                    ['blockquote', 'code-block'],
                
                    [{ 'header': 1 }, { 'header': 2 }],               // custom button values
                    [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                    [{ 'script': 'sub'}, { 'script': 'super' }],      // superscript/subscript
                    //[{ 'indent': '-1'}, { 'indent': '+1' }],           outdent/indent
                    //[{ 'direction': 'rtl' }],                         // text direction
                
                    //[{ 'size': ['small', false, 'large', 'huge'] }],  // custom dropdown
                    [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
                
                    [{ 'color': [] }, { 'background': [] }],          // dropdown with defaults from theme
                    [{ 'font': [] }],
                    [{ 'align': [] }],
                
                    ['clean'],                                         // remove formatting button
                
                    ['link']                                           // link and image, video
                    //['link', 'image', 'video']                          link and image, video
                  ]
            }],
            customOptions: [{
                import: 'formats/font',
                whitelist: ['mirza', 'roboto', 'aref', 'serif', 'sanserif', 'monospace']
            }]
        }),
        provideHttpClient(),
        provideRouter(appRoutes,
            withPreloading(PreloadAllModules),
            withInMemoryScrolling({scrollPositionRestoration: 'enabled'}),
            withHashLocation()
        ),

        // Material Date Adapter
        {
            provide : DateAdapter,
            useClass: LuxonDateAdapter,
        },
        {
            provide : MAT_DATE_FORMATS,
            useValue: {
                parse  : {
                    dateInput: 'dd/MM/yy',
                    timeInput: 'HH:mm'
                },
                display: {
                    dateInput         : 'dd/MM/yy',
                    monthYearLabel    : 'MMMM yyyy',
                    dateA11yLabel     : 'LL',
                    monthYearA11yLabel: 'MMMM yyyy',
                    timeInput: 'HH:mm:ss',
                },
            },
        },

        // Transloco Config
        provideTransloco({
            config: {
                availableLangs      : [
                    {
                        id   : 'br',
                        label: 'Brazilian',
                    },
                    {
                        id   : 'en',
                        label: 'English',
                    },
                    {
                        id   : 'tr',
                        label: 'Turkish',
                    },
                ],
                defaultLang         : 'br',
                fallbackLang        : 'br',
                reRenderOnLangChange: true,
                prodMode            : true,
            },
            loader: TranslocoHttpLoader,
        }),
        {
            // Preload the default language before the app starts to prevent empty/jumping content
            provide   : APP_INITIALIZER,
            useFactory: () =>
            {
                const translocoService = inject(TranslocoService);
                const defaultLang = translocoService.getDefaultLang();
                translocoService.setActiveLang(defaultLang);

                return () => firstValueFrom(translocoService.load(defaultLang));
            },
            multi     : true,
        },

        // Fuse
        provideAuth(),
        provideIcons(),
        provideFuse({
            mockApi: {
                delay   : 0,
                services: mockApiServices,
            },
            fuse   : {
                layout : 'thin',
                scheme : 'light',
                screens: {
                    sm: '600px',
                    md: '960px',
                    lg: '1280px',
                    xl: '1440px',
                },
                theme  : 'theme-default',
                themes : [
                    {
                        id  : 'theme-default',
                        name: 'Default',
                    },
                    {
                        id  : 'theme-brand',
                        name: 'Brand',
                    },
                    {
                        id  : 'theme-teal',
                        name: 'Teal',
                    },
                    {
                        id  : 'theme-rose',
                        name: 'Rose',
                    },
                    {
                        id  : 'theme-purple',
                        name: 'Purple',
                    },
                    {
                        id  : 'theme-amber',
                        name: 'Amber',
                    },
                ],
            },
        }),
    ],
};
