import {Component} from '@angular/core';
import Error = protractor.error.Error;
import {AlertMessage, ALERT_TYPE_INFO} from "./alert-message";
import {OnDestroy, OnInit} from "@angular/core/esm";
import {BroadcasterService} from "../../common/services/broadcaster.service";

@Component({
  selector: 'error',
  providers: [],
  templateUrl: './alert.component.html'
})
export class AlertComponent implements OnInit, OnDestroy {

  constructor(private broadcasterService: BroadcasterService) {

  }

  alertMessages: AlertMessage[] = [];

  ngOnInit(): void {
    this.broadcasterService.on<AlertMessage>('alertComponent').subscribe(
      alert => {
        this.alertMessages.push(alert);
      }
    )
  }

  ngOnDestroy(): void {

  }

}
