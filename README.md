# AudioTuristicGuide
Audio turistic guide is my intern project



 /* if distance between current location and selected marker is less then 1000 meter
 * Audio can be playable.
 * Purpose of the this condition is, provide an audio guide when user go to destination without turist guide as person.
 * People can have listener from museum. But this is not preferable due to corona virus or hygiene condition for some people.
 * We are giving chance to listen those audio guide from personel device.
 * Payment plan can be generable after a while.
 * For example premium plan:
 * All audios can be reachable without distance restriction
 * This is just an idea
 */


//  REFERENCES
 /* To get map and get location,googles MapsActivityCurrentPlace tutorial was used as reference.
  * https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
  */
//Important
 //API Keys and Restrictions
/* 2 api keys was used in this project
 * 1.to get current location
 *   this api is avaliable only android applications
 *   selected api's are  Geocoding API
                        Geolocation API
                        Distance Matrix API
                        Directions API
                        Maps Elevation API
                        Maps Embed API
                        Maps SDK for Android
                        Places API
                        Roads API
                        ***just Maps SDK for Android ,Geocoding API and Geolocation API are using inside project
                        ***sha key finger print belongs to my personal computer
 *
 *
 * 2. to get direction
 * its restriction is ip addresses.
 **************** IMPORTANT ********************
 * When you test the project ,  IPv4 or IPv6  address should be added.
 * http://console.cloud.google.com/
Selected api's are  Directions API
                    Geocoding API
                    Geolocation API
 */

// Important
/* Current location is now shown in my android virtual device (Nexus 5 API 30).
   My location is also not visible google maps application inside avd.There is a problem with avd.
   But when i tested it on real device,it shows the correct current location
*/
