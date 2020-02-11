// Only the functions marked IN USE are currently in use for injecting headers and verifying JWT. The functions not marked as so were for different attempts at a JWT verification solution.
// The reason I left them in is in case we want to try and do JWT verification here in nginx again.

// IN USE for header injection
function parseIDToken(idToken, s) {
  var resStrParts = idToken.split(".");

  if (resStrParts.length !== 3) {
    s.log("ID Token was not 3 parts long");
    return {};
  }

  var str = String.bytesFrom(resStrParts[1], "base64");
  return JSON.parse(str);
}

// IN USE for header injection
function getUserID(r) {
  if (r.headersIn["idToken"] !== undefined) {
    return parseIDToken(r.headersIn["idToken"], r)["sub"];
  }
  return "";
}

// IN USE for JWT verification (this extracts one of two tokens that will be present)
function getToken(r) {
  if (r.headersIn["idToken"] != undefined) {
    return r.headersIn["idToken"];
  }
  if (r.headersIn["Refresh-Token"] != undefined) {
    return r.headersIn["Refresh-Token"];
  }
  return "";
}
