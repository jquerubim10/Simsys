import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable()
export class HttpCoreInterceptor implements HttpInterceptor {
    intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {

        let accessToken: any = JSON.parse(localStorage.getItem('loggedUser')).token || '';
        
        // Clone the request to add the new headers
        req = req.clone({

            setHeaders: {
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'Authorization': accessToken ? `Bearer ${accessToken}` : ''
            }
        });

        return next.handle(req);
    }
}