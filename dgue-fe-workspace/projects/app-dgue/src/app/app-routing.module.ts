import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DGUEHomeComponent } from 'projects/app-dgue/src/app/layout/pages/dgue-home/dgue-home.component';
import { ProceduraComponent } from './layout/pages/procedura/procedura.component';
import { EsclusioneComponent } from './layout/pages/esclusione/esclusione.component';
import { SelezioneComponent } from './layout/pages/selezione/selezione.component';
import { FineComponent } from './layout/pages/fine/fine.component';
import { AvvioComponent } from './layout/pages/avvio/avvio.component';
import { QuadroGeneraleComponent } from './layout/pages/quadro-generale/quadro-generale.component';
import { DGUEHomePortaleComponent } from './layout/pages/dgue-home-portale/dgue-home-portale.component';
import { DGUEHomeAppaltiComponent } from './layout/pages/dgue-home-appalti/dgue-home-appalti.component';
import { QuadroGeneraleAppaltiComponent } from './layout/pages/quadro-generale-appalti/quadro-generale-appalti.component';
import { CookiesComponent } from './layout/pages/footer/cookies.component';
import { CreditsComponent } from './layout/pages/footer/credits.component';
import { PrivacyComponent } from './layout/pages/footer/privacy.component';
import { DisclaimerComponent } from './layout/pages/footer/disclaimer.component';
import { AssistenzaComponent } from './layout/pages/footer/assistenza.component';
import { QuadroGeneraleCaricamentoComponent } from './layout/pages/quadro-generale-caricamento/quadro-generale-caricamento.component';


const routes: Routes = [
  {path:"dgue-home", component:DGUEHomeComponent},
  {path:"dgue-home-portale", component:DGUEHomePortaleComponent},
  {path:"avvio", component:AvvioComponent},
  {path:"procedura", component:ProceduraComponent},
  {path:"esclusione", component:EsclusioneComponent},
  {path:"selezione", component:SelezioneComponent},
  {path:"fine", component:FineComponent},
  {path:"quadro-generale", component:QuadroGeneraleComponent},
  {path:"dgue-home-appalti", component:DGUEHomeAppaltiComponent},  
  {path:"quadro-generale-appalti", component:QuadroGeneraleAppaltiComponent},
  {path:"quadro-generale-caricamento", component:QuadroGeneraleCaricamentoComponent},  
  {path:"cookies", component:CookiesComponent},
  {path:"credits", component:CreditsComponent},
  {path:"privacy", component:PrivacyComponent},
  {path:"disclaimer", component:DisclaimerComponent},
  {path:"assistenza", component:AssistenzaComponent},
  { 
    path: '',
    redirectTo: '/dgue-home',
    pathMatch: 'full'
   }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }


