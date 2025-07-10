import { HttpClient } from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import { environment } from '../../environments/environment';
import { AppSettings } from '../model/app-settings.models';


@Injectable({providedIn: 'root'})
export class AppConfigService {

  private appConfig: AppSettings;

  constructor(private injector: Injector) {
    // Do nothing
  }

  public loadAppConfig(): Promise<AppSettings> {
    const http = this.injector.get(HttpClient);
    return http.get(environment.appSettingsUrl)
      .toPromise()
      .then((data) => {
        this.appConfig = data as AppSettings;
        return this.appConfig;
      });
  }

  public get config(): AppSettings {
    return this.appConfig;
  }
}
