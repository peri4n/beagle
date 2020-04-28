export interface SystemState {
    loggedIn: boolean
    userName: string
}

export const UPDATE_SESSION = 'LOGIN_USER'

interface LoginUserAction {
    type: typeof UPDATE_SESSION
    payload: SystemState
}

export type SystemActionTypes = LoginUserAction
