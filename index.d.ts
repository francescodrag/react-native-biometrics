declare module 'react-native-biometrics' {
  /**
   * Enum for touch id sensor type
   */
  const TouchID: string;
  /**
   * Enum for face id sensor type
   */
  const FaceID: string;

  interface SimplePromptOptions {
    promptMessage: string,
    descriptionMessage?: string,
    cancelButtonText?: string,
    isPromptVisible: string,
  }

  /**
   * Returns promise that resolves to null, TouchID, or FaceID
   * @returns {Promise} Promise that resolves to null, TouchID, or FaceID
   */
  function isSensorAvailable(): Promise<string>;
  /**
   * Prompts user with biometrics dialog using the passed in prompt message if
   * it is provided, returns promise that resolves to the public key of the
   * newly generated key pair
   * @param {string} promptMessage
   * @returns {Promise}  Promise that resolves to newly generated public key
   */
  function createKeys(promptMessage?: string): Promise<string>;
  /**
  * Returns promise that resolves to true or false indicating if the keys
  * were properly deleted
  * @returns {Promise} Promise that resolves to true or false
  */
  function deleteKeys(): Promise<boolean>;
  /**
   * Prompts user with biometrics dialog using the passed in prompt message and
   * returns promise that resolves to a cryptographic signature of the payload
   * @param {string} promptMessage
   * @param {string} payload
   * @returns {Promise}  Promise that resolves to cryptographic signature
   */
  function createSignature(
    promptMessage: string,
    payload: string
  ): Promise<string>;

  /**
    * Prompts user with biometrics dialog using the passed in prompt message and
    * returns promise that resolves to an object with object.success = true if the user passes,
    * object.success = false if the user cancels, and rejects if anything fails
    * @param {Object} simplePromptOptions
    * @param {string} simplePromptOptions.promptMessage
    * @param {string} simplePromptOptions.descriptionMessage (Android only)
    * @param {string} simplePromptOptions.cancelButtonText (Android only)
    * @param {string} simplePromptOptions.isPromptVisible (Android only)
  * @returns {Promise}  Promise that resolves if the user passes, and
  * rejects if the user fails or cancels
  */
  function simplePrompt(simplePromptOptions: SimplePromptOptions): Promise<boolean>;
}
