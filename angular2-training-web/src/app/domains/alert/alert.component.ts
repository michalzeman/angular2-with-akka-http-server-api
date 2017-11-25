import {Component, OnInit, OnDestroy} from '@angular/core';
// import Error = protractor.error.Error;
import {AlertMessage, ALERT_TYPE_INFO} from "./alert-message";
import {BroadcasterService} from "../../common/services/broadcaster.service";
import {Subscription} from "rxjs/Subscription";

@Component({
  selector: 'error',
  providers: [],
  templateUrl: './alert.component.html'
})
export class AlertComponent implements OnInit, OnDestroy {

  constructor(private broadcasterService: BroadcasterService) {

  }

  private eventSub: Subscription = Subscription.EMPTY;

  alertMessages: AlertMessage[] = [];

  ngOnInit(): void {
    this.eventSub = this.broadcasterService.on<AlertMessage>('alertComponent').subscribe(
      alert => {
        this.alertMessages.push(alert);
      }
    )
  }

  ngOnDestroy(): void {
    if (!this.eventSub.closed) {
      this.eventSub.unsubscribe();
    }
  }

}
