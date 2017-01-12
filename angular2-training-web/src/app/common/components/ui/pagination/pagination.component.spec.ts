import {
  TestBed,
  inject
} from '@angular/core/testing';

import {FormControlService} from "../../common/templates/form-control.service";
import {TestService} from "./test.service";
import {TestComponent} from "./test.component";
import {ActivatedRoute, Router, Params} from "@angular/router";
import {BaseRequestOptions, Http, ResponseOptions, Response} from "@angular/http";
import {MockBackend} from "@angular/http/testing";
import {Test} from "./test";
import {Observable} from "rxjs/Rx";
import {DelegateService} from "../../common/services/delegate.service";
import {TestTemplate} from "./test-template";
import {BroadcastEmmitterService} from "../../common/services/broadcast-emitter.servie";
import {BroadcasterService} from "../../common/services/broadcaster.service";
import {PaginationComponent} from "./pagination.component";
import {PaginationModel} from "./pagination-model";

describe('PartitionComponent', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
        providers: [PaginationComponent]
      }
    );
  });

  it('PartitionComponent - test', inject([PaginationComponent], (testStub) => {
    testStub.pgnStartItems = 5;
    testStub.pgnEndItems = 5;

    let pgnModelOds: Observable<PaginationModel> = Observable.create(observer => {
      observer.next(new PaginationModel(5, 1, 7, ""));
    });

    testStub.pgnModelOds = pgnModelOds;
    testStub.ngOnInit();
    console.log(testStub.pgnStartArray);
    expect(testStub.pgnStartArray.every((item, index, array) => {
      return item > 0;
    })).toBe(true);
    expect(testStub.pgnEndArray.every((item, index, array) => {
      return item > 0;
    })).toBe(true);
  }));
});
