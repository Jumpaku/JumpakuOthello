import * as React from "react";

export interface HelloProps { message: string; }

export const Wellcome = (props: HelloProps) => <h1>Wellcome {props.message}!</h1>;