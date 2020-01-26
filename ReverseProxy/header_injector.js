
/**
*  Base64 encode / decode
*  http://www.webtoolkit.info/
**/

var Base64 = {
    // private property
    _keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

    // public method for encoding
    encode: function (input) {
        var output = "";
        var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        var i = 0;

        input = Base64._utf8_encode(input);

        while (i < input.length) {
            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);

            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;

            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }

            output = output +
                this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
                this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);
        }

        return output;
    },

    // public method for decoding
    decode: function (input) {
        var output = "";
        var chr1, chr2, chr3;
        var enc1, enc2, enc3, enc4;
        var i = 0;

        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

        while (i < input.length) {
            enc1 = this._keyStr.indexOf(input.charAt(i++));
            enc2 = this._keyStr.indexOf(input.charAt(i++));
            enc3 = this._keyStr.indexOf(input.charAt(i++));
            enc4 = this._keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output = output + String.fromCharCode(chr1);

            if (enc3 != 64) {
                output = output + String.fromCharCode(chr2);
            }
            if (enc4 != 64) {
                output = output + String.fromCharCode(chr3);
            }
        }

        output = Base64._utf8_decode(output);

        return output;
    },

    // private method for UTF-8 encoding
    _utf8_encode: function (string) {
        string = string.replace(/\r\n/g, "\n");
        var utftext = "";

        for (var n = 0; n < string.length; n++) {
            var c = string.charCodeAt(n);

            if (c < 128) {
                utftext += String.fromCharCode(c);
            }
            else if ((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            }
            else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }
        }

        return utftext;
    },

    // private method for UTF-8 decoding
    _utf8_decode: function (utftext) {
        var string = "";
        var i = 0;
        var c1 = 0
        var c2 = c1;
        var c = c2;
        var c3;

        while (i < utftext.length) {
            c = utftext.charCodeAt(i);

            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            }
            else if ((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i + 1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            }
            else {
                c2 = utftext.charCodeAt(i + 1);
                c3 = utftext.charCodeAt(i + 2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }
        }

        return string;
    }
}

function getUserID(r) {
    for (var h in r.headersIn) {
        if (h == "idToken") {
            var userID = parseIDToken(r.headersIn[h], r)["sub"];
            return userID;
        }
    }
    return "";
}

function verifyJWT(r) {
    for (var h in r.headersIn) {
        if (h == "idToken") {
            var crypt = require("crypto");
            // Step 1 check that the token is well formed.
            var token = r.headersIn[h];
            var tokenParts = token.split(".");

            if (tokenParts.length != 3) {
                return false;
            }

            // Decode the header and make sure no line breaks, whitespace have been used and make sure the 
            // decoded value is a valid JSON object
            var header = Base64.decode(tokenParts[0]);
            if (!validateJWTCharacters(header, r)) {
                r.log("Header contains invalid characters");
                return false;
            }
            r.log("header" + header);
            // verify the header is a valid JSON token
            if ((header = validateJSONStructure(header, r)) == false) {
                r.log("Header not valid JSON");
                return false
            };
            var payload = Base64.decode(tokenParts[1]);
            // verify the payload only contains valid characters
            if (!validateJWTCharacters(payload, r)) {
                r.log("Payload contains invalid characters");
                return false
            };
            // verify the payload is a valid JSON token
            if ((payload = validateJSONStructure(payload, r)) == false) {
                r.log("Payload not valid JSON");
                return false
            };
            var signature = Base64.decode(tokenParts[2]);
            // verify the signature only contains valid characters
            if (!validateJWTCharacters(signature, r)) {
                r.log("Signature contains invalid characters");
                return false
            };
            // the signature isn't supposed to be JSON so we don't validate its JSON strucutre.

            var alg = header["alg"];

            var tokenKid = header["kid"]
            r.log("tokenKid = " + tokenKid);
            var keys = [
                {
                    "alg": "RS256",
                    "e": "AQAB",
                    "kid": "EjCheGie1FqzpOFuB5iM5RppOffIVb1SE3m+GNqB2ag=",
                    "kty": "RSA",
                    "n": "lkK-0aUj7_W6DANSw8CFxKKirZvZtVu2GlfrPybs5sQuJQGRa9ZpPcy3ysb3-LtpAH9qiFym6oObB2eQGQGXg0jSv-SMuVHBje3zQ71y-Zld2xyh8dhNr_a-WNHiClgrlP3VG-P2Op5MLeCyw1WHeBZa6hhwFJsj0DmTOQH-AAt7z47MJRqms2df0TjFujcHY__9s24XXNtl0yhjkTZ1ducI3i5x2Fu5d2KIUFJjwCKmOd7qsOH876bQoL9FQnXWKrpeaEQfkbAsSDmJBsK9wyTO0CMNQ7kR5uw6W5s4T3bcPrjRrLqvSd2L2XNk9qVLqN1-iN1HIjP0BDNHsZVN7w",
                    "use": "sig"
                },
                {
                    "alg": "RS256",
                    "e": "AQAB",
                    "kid": "29lsQPMzOB19bO72WkzNdkusng3MLPlgVnXsDY7vn8Y=",
                    "kty": "RSA",
                    "n": "ghzJjNC3S24I9riLnMmAJNbnHNqDXamdpJjZC-lvDHtZgBdqRNYbf-uZN-5jKQqFekVgsxoz2V5-T09mLJA440Y0ubiUXa4TTwpEKOigW_KsYjhU00hhF6Ni1XfW-sOkClmWPs_2Vw_cJbB47hsqVFfPLsghIzyCXh18OYPK4QTKEFr-OO1D1Qigh1ByckipIHZ8Mq10jaUOQpBj30601XJzbauprgNpVLU8fHxC1VgDhLUgt--Tyj3ACK3vi5rjmONi92J36f46hYePtA0pvVQPsyq-y0lYyWFMs1aPJdZSmGXfx83f1CaEBdWXsWFf8YeoE5GQqy3TLKQ0xUQ_5Q",
                    "use": "sig"
                }
            ];


            // verify that the token is signed using the correct algorithm.
            if (alg != "RS256") {
                return false;
            };

            var key;
            var n;
            for (var i in keys) {
                r.log(key);
                if (keys[i].kid == tokenKid) {
                    key = keys[i].kid;
                    n = keys[i].n;
                    break;
                }
            }

            var jwt = create_hs256_jwt(tokenParts[0], tokenParts[1], key);
            var jwtWithN = create_hs256_jwt(tokenParts[0], tokenParts[1], n);

            if (jwtWithN == tokenParts[2]) {
                r.log("jwtWithN works");
            }

            // verify that the signature we generated base on the claims matches the one 
            if (jwt != tokenParts[2]) {
                // r.log(jwt);
                // r.log(tokenParts[2]);
                // r.log("Signatures dont match");
                return false
            };


            // check expiry date

            var expiry = payload["exp"];

            if ((Date.now() / 1000) >= expiry) { // if the token expires now or in the past then this token is invalid.
                r.log("token expired");
                return false;
            }

            // check token issuer

            if (payload["iss"] != "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_vBLWU8p2J") {
                r.log("Issuers don't match");
                return false;
            }

            return true;
        }
    }
}

function create_hs256_jwt(header, claims, key) {

    var s = JSON.stringify(header).toBytes().toString('base64url') + '.'
        + JSON.stringify(claims).toBytes().toString('base64url');

    var h = require('crypto').createHmac('sha256', key);

    return s + '.' + h.update(s).digest().toString('base64url');
}

function validateJWTCharacters(s, r) {
    s = s.slice(NaN, s.lastIndexOf("}") + 1);
    var match = s.match(/[\n\r\s]+/);
    if (match != undefined && match.length != 0) {
        r.log(s);
        r.log("Match = " + match);
        return false;
    }
    return true;
}

function validateJSONStructure(s, r) {
    s = s.slice(NaN, s.lastIndexOf("}") + 1);
    try {
        return JSON.parse(s);
    } catch (e) {
        r.log(e)
        return false;
    }
}

function parseIDToken(idToken, s) {
    var resStrParts = idToken.split(".");

    if (resStrParts.length !== 3) {
        s.log("ID Token was not 3 parts long");
        return {};
    }

    // s.log("about to Base64 decode " + resStrParts[1]);
    var str = Base64.decode(resStrParts[1]);
    // s.log("B64 Decoded = " + str);
    str = str.slice(NaN, str.lastIndexOf("}") + 1); // this needs to be sliced because there are invisible characters being appended to the end of the string that break the JSON.parse
    return JSON.parse(str);
}