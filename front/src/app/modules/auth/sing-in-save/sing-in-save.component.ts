import { Component, OnInit, ViewEncapsulation } from '@angular/core';

import { Router, ActivatedRoute } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { AuthService } from 'app/core/auth/auth.service';

@Component({
  selector: 'app-sing-in-save',
  imports: [],
  encapsulation: ViewEncapsulation.None,
  animations: fuseAnimations,
  standalone: true,
  templateUrl: './sing-in-save.component.html',
})
export class SingInSaveComponent implements OnInit {
  constructor(private router: Router, private route: ActivatedRoute, private _authService: AuthService) {}

  ngOnInit(): void {
    const username = this.route.snapshot.queryParams['username'];
    const pass = this.route.snapshot.queryParams['pass'];

    // Sign in
    this._authService.signInSave(username, pass).subscribe(
      () => {
          // Set the redirect url.
          // The '/signed-in-redirect' is a dummy url to catch the request and redirect the user
          // to the correct page after a successful sign in. This way, that url can be set via
          // routing file and we don't have to touch here.
          const redirectURL = this.route.snapshot.queryParamMap.get('redirectURL') || '/home';

          // Navigate to the redirect url
          this.router.navigateByUrl(redirectURL);
      },
      (response) => {},
    );
  }

}
