'use strict';

/**
 * CreationController
 * @constructor
 */
var CreationController = function($scope, $http) {
    var creationsArray = [],
        self = this;

    $scope.fetchCreationsList = function() {
        $http.get('creationlist.json').success(function(creationList){
            if(creationList && creationList.length > 0) {
                // Check whether the user is loggedIn and set scope username and uservalue to be used on saving, editing and deleting creations.
                var creationsLastItem = creationList.length - 1;
                if( (creationList[creationsLastItem].userValue !== null) && (creationList[creationsLastItem].title === "") ) {
                    var creation = creationList.pop();
                    $scope.username = creation.username;
                    $scope.userValue = creation.userValue;
                }  else if( (creationList[creationsLastItem].userValue !== null) && (creationList[creationsLastItem].title !== "") ) {
                    $scope.username = creationList[creationsLastItem].username;
                    $scope.userValue = creationList[creationsLastItem].userValue;
                }
                $scope.processCreationList(creationList);
            }
            else {
                $scope.errorMessage = "We were unable to connect to our server because it is momentarily down or because you have no internet connection.  Please try again now or later in the day.";
                $('#errorModal').modal('show');
            }
        }).error(function (data, status, headers, config) {
            $scope.errorMessage = "We were unable to connect to our server because it is momentarily down or because you have no internet connection.  Please try again now or later in the day.";
            $('#errorModal').modal('show');
        });
    };

    $scope.myCreations = function(username) {
        $('.dropdown.open').removeClass('open');
        var myCreationArray = [];

        creationsArray.forEach(function(creation) {
            if (creation.username === username) {
                myCreationArray.push(creation);
            }
        });
        $scope.onePersonsCreations = myCreationArray;
        $scope.creations = myCreationArray;
    };

    $scope.creationByTitle = function(title) {
        $('.dropdown.open').removeClass('open');
        var myCreationArray = [];

        creationsArray.forEach(function(creation) {
            if (creation.title === title) {
                myCreationArray.push(creation);
            }
        });
        $scope.creations = myCreationArray;
    };

    $scope.allCreations = function() {
        $http({
            url: 'allCreations',
            method: "GET",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
         }).success(function(creationList){
            if(creationList && creationList.length > 0) {
                $scope.creations = creationList;
                creationsArray = creationList;
                $scope.creators = [];

                creationList.forEach(function(value, index) {
                    if($.inArray(value.username, $scope.creators) === -1) {
                        $scope.creators.push(value.username);
                    }
                });
            }
            else {
                $scope.errorMessage = "We were unable to connect to our server because it is momentarily down or because you have no internet connection.  Please try again now or later in the day.";
                $('#errorModal').modal('show');
            }
       }).error(function (data, status, headers, config) {
            $scope.errorMessage = "We were unable to connect to our server because it is momentarily down or because you have no internet connection.  Please try again now or later in the day.";
            $('#errorModal').modal('show');
      });
    };

    $scope.popularCreations = function() {
        $http({
            url: 'popularCreations',
            method: "GET",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
         }).success(function(creationList){
            if(creationList && creationList.length > 0) {
                $scope.creations = creationList;
                creationsArray = creationList;
                $scope.creators = [];

                creationList.forEach(function(value, index) {
                    if($.inArray(value.username, $scope.creators) === -1) {
                        $scope.creators.push(value.username);
                    }
                });
            }
            else {
                $scope.errorMessage = "We were unable to connect to our server because it is momentarily down or because you have no internet connection.  Please try again now or later in the day.";
                $('#errorModal').modal('show');
            }
       }).error(function (data, status, headers, config) {
            $scope.errorMessage = "We were unable to connect to our server because it is momentarily down or because you have no internet connection.  Please try again now or later in the day.";
            $('#errorModal').modal('show');
      });
    };

    $scope.creationBySearch = function() {
        var searchCreationArray = [], //
        searchTermLower = $scope.searchTerm.toLowerCase();

        creationsArray.forEach(function(creation) {
            var creationTitleLower = creation.title.toLowerCase(), //
                creationPoemLower = creation.poem.toLowerCase(), //
                creationUsernameLower = creation.username.toLowerCase()
            if ( (creationTitleLower.indexOf(searchTermLower) >= 0) || (creationPoemLower.indexOf(searchTermLower) >= 0) || (creationUsernameLower.indexOf(searchTermLower) >= 0) ) {
                searchCreationArray.push(creation);
            }
        });
        $scope.creations = searchCreationArray;
    };

    $scope.about = function() {
        $('#aboutModal').modal('show');
    };

    $scope.contactUsLink = function() {
        $scope.name = null;
        $scope.email = null;
        $scope.subject = null;
        $scope.comment = null;
       $('#contactUsModal').modal('show');
    };

    $scope.contactUs = function() {
        if( ($scope.name && $scope.name !== "") && ($scope.email && $scope.email !== "") && ($scope.subject && $scope.subject !== "") && ($scope.comment && $scope.comment !== "") ) {
            if($scope.email.indexOf(".com") === -1) {
                $scope.email = $scope.email.concat(".com")
            };
            $http({
                url: 'contactUs',
                method: "POST",
                data: $.param({username: $scope.name, email: $scope.email, subject: $scope.subject, comment: $scope.comment}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function(data){
                if(!data.error) {
                    $('#contactUsModal').modal('hide');
                    $scope.confirmationMessage = data.response;
                    $('#confirmationModal').modal('show');
                } else {
                    $scope.errorMessage = data.error;
                    $('#errorModal').modal('show');
                }
            }).error(function (data, status, headers, config) {
               $scope.errorMessage = "There was a server error. Please try again now or later in the day.";
               $('#errorModal').modal('show');
            });
        } else {
            $scope.errorMessage = "Please complete all the fields.";
            $('#errorModal').modal('show');
        }
    };

    $scope.showLoggedInHeader = function() {
        $(".login-signup").css({"display": "none"});
        $(".mobile-login-signup").css({"display": "none"});
        $(".welcome-container").css({"display": "inline"});
        $(".mobile-welcome-container").css({"display": "inline"});
    };

    $scope.showLoginSignupHeader = function() {
        $scope.username = null;
        $scope.email = null;
        $scope.password = null;
        $(".login-signup").css({"display": "inline"});
        $(".mobile-login-signup").css({"display": "inline"});
        $(".welcome-container").css({"display": "none"});
        $(".mobile-welcome-container").css({"display": "none"});
    };

    $scope.signupButton = function() {
        $scope.username = null;
        $scope.email = null;
        $scope.password = null;
        $('#signupModal').modal('show');
    };

     $scope.signup = function() {
        if( ($scope.username && $scope.username !== "") && ($scope.email && $scope.email !== "") && ($scope.password && $scope.password !== "") ) {
            $http({
                url: 'signupCreator',
                method: "POST",
                data: $.param({username: $scope.username, email: $scope.email, password: $scope.password}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function(data){
                if(!data.responseValues.error) {
                    $scope.userValue = data.responseValues.response;
                    $scope.processCreationList(data.creations);
                    $('#signupModal').modal('hide');
                }
                else {
                    $scope.errorMessage = data.responseValues.error;
                    $('#errorModal').modal('show');
                }
            }).error(function (data, status, headers, config) {
               $scope.errorMessage = "We were unable to sign you on to our server.  Please try again now or later in the day.";
               $('#errorModal').modal('show');
            });
        } else {
            $scope.errorMessage = "Please type in your username, email and  password before clicking the signup button.";
            $('#errorModal').modal('show');
        }
    };

    $scope.loginButton = function() {
        $scope.email = null;
        $scope.password = null;
        $('#loginModal').modal('show');
    };

     $scope.login = function() {
        if( ($scope.email && $scope.email !== "") && ($scope.password && $scope.password !== "") ) {
            if($scope.email.indexOf(".com") === -1) {
                $scope.email = $scope.email.concat(".com")
            };
            $http({
                url: 'loginCreator',
                method: "POST",
                data: $.param({email: $scope.email, password: $scope.password}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
             }).success(function(data){
                if(!data.responseValues.error) {
                    $scope.userValue = data.responseValues.response;
                    $scope.username = data.responseValues.username;
                    $scope.processCreationList(data.creations);
                    $('#loginModal').modal('hide');
                } else {
                    $scope.errorMessage = data.responseValues.error;
                    $('#errorModal').modal('show');
                }
             }).error(function (data, status, headers, config) {
               $scope.errorMessage = "We were unable to log you in.  Please try again now or later in the day.";
               $('#errorModal').modal('show');
            });
        } else {
            $scope.errorMessage = "Please type in your full email and password before clicking the login button.";
            $('#errorModal').modal('show');
        }
    };

    $scope.logout = function() {
        $http({
            url: 'logoutCreator',
            method: "POST",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
         }).success(function(data){
            if(data.responseValues.response === "Server Transaction Success") {
                $scope.userValue = null;
                $scope.username = null;
                $scope.processCreationList(data.creations);
           } else {
                $scope.errorMessage = "You were not correctly logged out. Closing the browser will also log you out.";
                $('#errorModal').modal('show');
           }
       }).error(function (data, status, headers, config) {
            $scope.errorMessage = "You were not correctly logged out. Closing the browser will also log you out.";
            $('#errorModal').modal('show');
          });
    };

    $scope.forgotPasswordLink = function() {
        $scope.username = null;
        $scope.email = null;
        $('#forgotPasswordModal').modal('show');
    };

    $scope.forgotPassword = function() {
        if( ($scope.username && $scope.username !== "") && ($scope.email && $scope.email !== "") ) {
            if($scope.email.indexOf(".com") === -1) {
                $scope.email = $scope.email.concat(".com")
            };
            $http({
                url: 'forgotPassword',
                method: "POST",
                data: $.param({username: $scope.username, email: $scope.email}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
             }).success(function(data){
                if(!data.error) {
                    $('#forgotPasswordModal').modal('hide');
                    $scope.confirmationMessage = data.response;
                    $('#confirmationModal').modal('show');
                } else {
                    $scope.errorMessage = data.error;
                    $('#errorModal').modal('show');
                }
             }).error(function (data, status, headers, config) {
               $scope.errorMessage = "There was a server error. Please try again now or later in the day.";
               $('#errorModal').modal('show');
            });
        } else {
            $scope.errorMessage = "Please type in your full email before clicking the enter button.";
            $('#errorModal').modal('show');
        }
    };

    $scope.changeEmailLink = function() {
        $scope.oldEmail = null;
        $scope.newEmail1 = null;
        $scope.newEmail2 = null;
        $('#changeEmailModal').modal('show');
    };

    $scope.changeEmail = function() {
        if( ($scope.oldEmail && $scope.oldEmail !== "") &&  ($scope.newEmail1 && $scope.newEmail1 !== "") && ($scope.newEmail2 && $scope.newEmail2 !== "") ){
            if($scope.newEmail1 === $scope.newEmail2) {
                $http({
                    url: 'changeEmail',
                    method: "POST",
                    data: $.param({username: $scope.username, userValue: $scope.userValue, oldEmail: $scope.oldEmail, newEmail: $scope.newEmail1}),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                 }).success(function(data){
                    if(!data.error) {
                        $('#changeEmailModal').modal('hide');
                        $scope.confirmationMessage = data.response;
                        $('#confirmationModal').modal('show');
                    } else {
                        $scope.errorMessage = data.error;
                        $('#errorModal').modal('show');
                    }
                 }).error(function (data, status, headers, config) {
                   $scope.errorMessage = "There was a server error. Please try again now or later in the day.";
                   $('#errorModal').modal('show');
                });
            } else {
                $scope.errorMessage = "The new email is not the same in both fields. Please complete them again.";
                $('#errorModal').modal('show');
            }
        } else {
            $scope.errorMessage = "Please complete the fields before clicking the enter button.";
            $('#errorModal').modal('show');
        }
    };

    $scope.changePasswordLink = function() {
        $scope.oldPassword = null;
        $scope.newPassword1 = null;
        $scope.newPassword2 = null;
        $('#changePasswordModal').modal('show');
    };

    $scope.changePassword = function() {
        if( ($scope.oldPassword && $scope.oldPassword !== "") &&  ($scope.newPassword1 && $scope.newPassword1 !== "") && ($scope.newPassword2 && $scope.newPassword2 !== "") ){
            if($scope.newPassword1 === $scope.newPassword2) {
                $http({
                    url: 'changePassword',
                    method: "POST",
                    data: $.param({username: $scope.username, userValue: $scope.userValue, email: $scope.email, oldPassword: $scope.oldPassword, newPassword: $scope.newPassword1}),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                 }).success(function(data){
                    if(!data.error) {
                        $('#changePasswordModal').modal('hide');
                        $scope.confirmationMessage = data.response;
                        $('#confirmationModal').modal('show');
                    } else {
                        $scope.errorMessage = data.error;
                        $('#errorModal').modal('show');
                    }
                 }).error(function (data, status, headers, config) {
                   $scope.errorMessage = "There was a server error. Please try again now or later in the day.";
                   $('#errorModal').modal('show');
                });
            } else {
                $scope.errorMessage = "The new password is not the same in both fields. Please complete them again.";
                $('#errorModal').modal('show');
            }
        } else {
            $scope.errorMessage = "Please complete the fields before clicking the enter button.";
            $('#errorModal').modal('show');
        }
    };

    $scope.processCreationList = function(creationList) {
        $scope.creations = creationList;
        creationsArray = creationList;
        $scope.creators = [];
        $scope.creationsTitleListForLoggedInUser = [];

        creationList.forEach(function(value, index) {
            if($.inArray(value.username, $scope.creators) === -1) {
                $scope.creators.push(value.username);
            }
        });

        if($scope.username) {
            $scope.isLoggedIn = true;
            $scope.showLoggedInHeader();
        }
        else {
            $scope.isLoggedIn = false;
            $scope.showLoginSignupHeader();
        }
    }

    $scope.readMoreRequest = function(title, poem, username) {
        $scope.readTitle = title;
        $scope.readPoem = poem;
        $('#readMoreModal').modal('show');

        $http({
            url: 'countReadMoreClick',
            method: "POST",
            data: $.param({title: title, username: username}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        });
    }

    $scope.saveRequest = function(title, poem) {
        $scope.saveTitle = "";
        $scope.savePoem = "";
        $scope.savePrivate = false;
        $('#newCreationModal').modal('show');
    }

     $scope.saveCreation = function() {
        if( ($scope.saveTitle && $scope.saveTitle !== "") && ($scope.savePoem && $scope.savePoem !== "")) {
            // Logged in user should not be able to save title already in use.
            var titleLowerCase = $scope.saveTitle.toLowerCase();
            if($.inArray(titleLowerCase, $scope.creationsTitleListForLoggedInUser) === -1) {
                $http({
                    url: 'saveCreation',
                    method: "POST",
                    data: $.param({username: $scope.username, title: $scope.saveTitle, poem: $scope.savePoem, userValue: $scope.userValue, isPrivate: $scope.savePrivate}),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                 }).success(function(data){
                    if(data.response === "Server Transaction Success") {
                        var savedObject = {
                            username: $scope.username,
                            title: $scope.saveTitle,
                            poem: $scope.savePoem,
                            isPrivate: $scope.savePrivate
                        }
                        $scope.creations.push(savedObject);
                        $scope.saveTitle = null;
                        $scope.savePoem = null;
                        $scope.savePrivate = false;
                        $('#newCreationModal').modal('hide');
                    }
                    else {
                        $scope.errorMessage = "The new creation was not saved correctly to the server. Please try again now or later in the day.";
                        $('#errorModal').modal('show');
                    }
                 }).error(function (data, status, headers, config) {
                    $scope.errorMessage = "The new creation was not saved correctly to the server. Please try again now or later in the day.";
                    $('#errorModal').modal('show');
                 });
            } else {
                $scope.errorMessage = "This title has already been used on a different creation. Please use a different title.";
                $('#errorModal').modal('show');
            }
        } else {
            $scope.errorMessage = "The creation title or content is empty. Please complete both.";
            $('#errorModal').modal('show');
        }
    };

    $scope.updateRequest = function(title, poem, isPrivate) {
        $scope.updateTitle = title;
        $scope.updatePoem = poem;
        $scope.updatePrivate = isPrivate;
        $('#updateCreationModal').modal('show');
    }

    $scope.updateCreation = function() {
        if( ($scope.updateTitle && $scope.updateTitle !== "") && ($scope.updatePoem && $scope.updatePoem !== "")) {
            $http({
                url: 'updateCreation',
                method: "POST",
                data: $.param({title: $scope.updateTitle, poem: $scope.updatePoem, username: $scope.username, userValue: $scope.userValue, isPrivate: $scope.updatePrivate}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
             }).success(function(data){
                if(data.response === "Server Transaction Success") {
                    // Update myCreation list
                    $scope.creations.forEach(function(myCreation, index) {
                        if(myCreation.title == $scope.updateTitle) {
                            $scope.creations[index].poem = $scope.updatePoem;
                            $scope.creations[index].isPrivate = $scope.updatePrivate;
                        }
                    });
                    $scope.updateTitle = null;
                    $scope.updatePoem = null;
                    $scope.updatePrivate = false;
                    $('#updateCreationModal').modal('hide');
                } else {
                    $scope.errorMessage = "The new creation was not updated correctly to the server. Please try again now or later in the day.";
                    $('#errorModal').modal('show');
                }
             }).error(function (data, status, headers, config) {
                    $scope.errorMessage = "The new creation was not updated correctly to the server. Please try again now or later in the day.";
                    $('#errorModal').modal('show');
             });
         } else {
             $scope.errorMessage = "The creation title or content is empty. Please complete both.";
             $('#errorModal').modal('show');
         }
    };

    $scope.deleteRequest = function(title) {
        $scope.deleteTitle = title;
        $('#deleteCreationModal').modal('show');
    }

    $scope.deleteCreation = function() {
        $http({
            url: 'deleteCreation',
            method: "POST",
            data: $.param({title: $scope.deleteTitle, username: $scope.username, userValue: $scope.userValue}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
         }).success(function(data){
            if(data.response === "Server Transaction Success") {
                $scope.creations = $scope.creations.filter(function (creation) {
                    return creation.title !== $scope.deleteTitle;
                });
                $scope.deleteTitle = null;
                $('#deleteCreationModal').modal('hide');
            }
         }).error(function (data, status, headers, config) {
            alert("The new creation was not deleted correctly to the server. Please try again now or later in the day.");
         });
    };

    // Starting App.
    $scope.fetchCreationsList();

    $scope.$on('ngRepeatFinished', function (ngRepeatFinishedEvent) {
//        $('.description').elastic();
        $('.description').addClass('no-expand');

         /// This is for the loading icon on a button ... still to implement correctly.
        $(function(){
            $('a, button').click(function() {
                $(this).toggleClass('active');
            });
        });

        $('.modal').on('shown.bs.modal', function () {
            var firstInput = $(this).find('input:visible:first');

            if(firstInput.length > 0) {
                setTimeout(function() {
                   firstInput.focus();
                }, 200);
            }
        })

        $(document).keypress(function(e) {
            if(e.which === 13) {
                // enter has been pressed, execute a click on enter-button.
                $('.modal.in .enter-button').click();
            }
        });
    });
};