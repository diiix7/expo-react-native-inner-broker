import {
  NativeModulesProxy,
  EventEmitter,
  Subscription,
} from "expo-modules-core";

import BrokerModule from "./BrokerModule";

export function hello(): string {
  return BrokerModule.hello();
}

export async function setValueAsync(value: string) {
  return await BrokerModule.setValueAsync(value);
}

export function startBroker(): boolean {
  return BrokerModule.startBroker();
}

const emitter = new EventEmitter(BrokerModule ?? NativeModulesProxy.Broker);

export function addChangeListener(
  listener: (event: any) => void,
): Subscription {
  return emitter.addListener<any>("onChange", listener);
}
