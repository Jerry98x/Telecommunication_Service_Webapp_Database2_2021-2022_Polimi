/**
 *
 */

(function () {
    if(sessionStorage.getItem("rejectedOrderId") != null){
        window.addEventListener("load", () => {
            let rejectedIdForm = document.createElement("form");
            rejectedIdForm.name = "rejectedIdForm";
            let input = document.createElement("input");
            input.name = "rejectedId"
            input.value = sessionStorage.getItem("rejectedOrderID");
            rejectedIdForm.appendChild(input);
            window.addEventListener("load", () => {
                makeCall("POST", "GetRejectedOrderToComplete", rejectedIdForm,
                    function (req) {
                        if (req.readyState === XMLHttpRequest.DONE) {
                            var message = req.responseText;
                            switch (req.status) {
                                case 200:
                                    //TODO show rejected order
                                    break;
                                default:
                                    document.getElementById("errormessage").textContent += message;
                                    break;
                            }
                        }
                    })
            })
        })
    } else {
        window.addEventListener("load", () => {
            let sptb = JSON.parse(sessionStorage.getItem("servicePackageToBuy"));
            let tot = JSON.parse(sessionStorage.getItem("pendingOrder")).totalCost;
            let cvp = JSON.parse(sessionStorage.getItem("pendingOrder")).chosenValidityPeriod;
            let cops = JSON.parse(sessionStorage.getItem("pendingOrder")).chosenOptionalProducts;

            if (sessionStorage.getItem("loggedUser") == null) {
                document.getElementById("errormessage").innerHTML = "You need to be logged in to complete a payment";
                //disable payment buttons
                document.getElementById("successfulPaymentBtn").disabled = true;
                document.getElementById("failingPaymentBtn").disabled = true;
                //add event listener to login/signup buttons
                document.getElementById("loginBtn").addEventListener("click", (event) => notLoggedRedirect(event));
                document.getElementById("signUpBtn").addEventListener("click", (event) => notLoggedRedirect(event));
                //show login/signup buttons
                document.getElementById("loginBtn").hidden = false;
                document.getElementById("signUpBtn").hidden = false;
            }

            document.getElementById("packageName").innerHTML = sptb.name;
            sptb.servicesDescriptions.forEach(sd => showServiceDescription(sd));
            if (cops != null && cops.length > 0) {
                document.getElementById("optionalProductsDiv").hidden = false;
                cops.forEach(cop => showOptionalProduct(cop));
            }
            document.getElementById("validityPeriodDiv").innerHTML = cvp.monthsOfValidity + " months at " + cvp.monthlyFee_euro + "€/month";
            document.getElementById("totalCost").innerHTML = tot;


            document.getElementById("successfulPaymentBtn").addEventListener("click", (event) => sendPayment(event, true));
            document.getElementById("failingPaymentBtn").addEventListener("click", (event) => sendPayment(event, false));
        });
    }

})();

function notLoggedRedirect(event){
    event.preventDefault();
    window.location.href = "LandingPage.html";
}

function showServiceDescription(serviceDescription){
    let service = document.createElement("p");
    service.innerHTML = serviceDescription;
    document.getElementById("servicesDiv").appendChild(service);
}

function showOptionalProduct(optionalProduct){
    if(optionalProduct != null) {
        let singleProductDiv = document.createElement("div");
        let opLabel = document.createElement("label");
        opLabel.innerHTML = optionalProduct.name + ": " + optionalProduct.monthlyFee_euro + "€/month";
        singleProductDiv.appendChild(opLabel);
        document.getElementById("optionalProductsDiv").appendChild(singleProductDiv);
    }
}

function sendPayment(event, isSuccessful) {
    event.preventDefault();
    let newOrderForm = document.getElementById("newOrderForm");
    let newOrder = JSON.parse(sessionStorage.getItem("pendingOrder"));
    newOrder.valid = isSuccessful ? 1 : 0;
    //TODO dates
    newOrderForm.appendChild(JSON.stringify(newOrder));
    makeCall("POST", "CreateOrder", newOrderForm,
        function (req) {
            if (req.readyState === XMLHttpRequest.DONE) {
                var message = req.responseText;
                switch (req.status) {
                    case 200:
                        console.log(sessionStorage.getItem(JSON.parse("pendingOrder")));
                        sessionStorage.removeItem("pendingOrder");
                        sessionStorage.removeItem("servicePackageToBuy");
                        window.location.href = "LandingPage.html";
                        break;
                    default:
                        document.getElementById("errormessage").textContent += message;
                        break;
                }
            }
        });
}