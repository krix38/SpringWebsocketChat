var Reflux = require("reflux");
var React = require("react");
var ReactDOM = require("react-dom");
var MActions = require("../actions/MessagesActions.js");
var MStore = require("../stores/MessagesStore.js");
var UActions = require("../actions/UsersActions.js");
var UStore = require("../stores/UsersStore.js");
var wsc = require("../websocketConnector/wsConnect.js");

var WsConnector = wsc.WsConnector;
var MessageActions = MActions.MessageActions;
var MessagesStore = MStore.MessageStore;
var UsersActions = UActions.UsersActions;
var UsersStore = UStore.UsersStore;

WsConnector.messagesStore = MessagesStore;
WsConnector.usersStore = UsersStore;
MessagesStore.wsConnector = WsConnector;
UsersStore.messagesStore = MessagesStore;

var navbarButtonsMounted = false;

var csrfName = null;
var csrfValue = null;

var setLoginMessageCallback = null;
var setConfigurationMessageCallback = null;

function renderMainView(){
	$.ajax({
		url: "jsonApi/getConnectedUsers",
		dataType: 'json',
		cache: false,
		success: function(data) {
			mountChatView(data);
		},
		error: function(xhr, status, err) {
            csrfName = xhr.getResponseHeader('X-CSRF-PARAM');
            csrfValue = xhr.getResponseHeader('X-CSRF-TOKEN');
			mountLogin();
		}
	});
}

function unmountContent(){
	ReactDOM.unmountComponentAtNode(document.getElementById('content'));
}

function unmountNavbar(){
	ReactDOM.unmountComponentAtNode(document.getElementById('navbar'));
}

function mountPasswordChangeView(){
    ReactDOM.render(<PasswordChangeView />, document.getElementById('content'));
}

function mountLogin(){
    ReactDOM.render(React.createElement(LoginForm, null), document.getElementById('content'));
}

function mountChatView(data){
    ReactDOM.render(<MainChatView users={data}/>, document.getElementById('content'));
}

function mountAdminPanelView(){
    ReactDOM.render(<AdminPanelView />, document.getElementById('content'));
}

var FormStyle = { paddingTop: "5%" };
var TableCellStyle = { padding: "5%"};
var InputStyle = { padding: "5%"};

var ReloadAdminPanelUserList = null;

var AvailableRoles = [
    {
      roleType: "ROLE_ADMIN",
      name: "admin"
    },
    {
      roleType: "ROLE_USER",
      name: "user"
    }
];

var AvailableEnabledValues = [
    {
        name: "enabled",
        value: "true"
    },
    {
        name: "disabled",
        value: "false"
    }
];

function mountNavbarButtons(){
    $.ajax({
        url: "getRole",
        dataType: 'text',
        cache: false,
        success: function(roleInfo) {
            ReactDOM.render(
                <NavbarButtons role={roleInfo} />,
                document.getElementById('navbar')
            );
        },
        error: function(xhr, status, err) {
            alert("getrole failed");
        }
    });
}

var NavButtonStyle = {
    cursor: "pointer"
}

var UserModifyCellStyle = {
    paddingLeft: "50%"
}

var lowerRowPaddingStyle = {
    paddingTop: "3%"
}

var AdminPanelView = React.createClass({
    handleBackButton: function(){
        unmountContent();
        renderMainView();
    },
    render: function(){
        return(
            <div style={FormStyle} className="adminPanelView">
                <div className="row">
                    <div className="col-sm-9">
                        <button type="button" className="btn btn-link" onClick={this.handleBackButton}>Back to main view</button>
                    </div>
                </div>
                <div style={lowerRowPaddingStyle} className="row">
                    <div className="col-xs-8 col-sm-6">
                        <UserAddForm />
                    </div>
                    <div className="col-xs-4 col-sm-6">
                        <UserModifyForm />
                    </div>
                </div>
                <ConfigurationMessageBox/>
            </div>
        )
    }
});

var UserModifyForm = React.createClass({
    getInitialState: function() {
        var users = [];
        return {allUsers: users};
    },
    showUserDetails: function(e){
        var user = JSON.parse(e.target.value);
        this.refs.userName.value = user.login;
        this.refs.selectedUserRole.value = user.role;
        this.refs.selectedUserEnabled.value = user.enabled;
    },
    updateUsers: function(){
        $.ajax({
            url: "admin/getAllUsers",
            dataType: 'json',
            cache: false,
            success: function(users) {
                this.setState({allUsers: users})
            }.bind(this),
            error: function(xhr, status, err) {
                setConfigurationMessageCallback("fetching all users failed");
            }
        });
    },
    componentDidMount: function(){
        this.updateUsers();
        ReloadAdminPanelUserList = this.updateUsers;
    },
    deleteUser: function(){
        var userName = this.refs.userName.value
        if(userName == "" || userName == "admin"){
            setConfigurationMessageCallback("wrong username");
        }else{
            var postData = {
              userName: userName,
            }
            postData[csrfName] = UsersStore.CSRF;
            $.ajax({
              url: "admin/deleteUser",
              dataType: "text",
              type: "POST",
              data: postData,
              success: function(data){
                setConfigurationMessageCallback("user deleted");
                ReloadAdminPanelUserList();
              }.bind(this),
              error: function(xhr, status, err){
                setConfigurationMessageCallback(xhr.responseText);
              }
             });
        }
    },
    modifyUser: function(){
        var userName = this.refs.userName.value
        var password = this.refs.password.value
        var role = this.refs.selectedUserRole.value
        var enabled = this.refs.selectedUserEnabled.value
        if(userName == ""){
            setConfigurationMessageCallback("wrong username");
        }else{
            if(userName == "admin"){
                role = "admin"
                enabled = "true"
            }
            var postData = {
              userName: userName,
              password: password,
              role: role,
              enabled: enabled
            }
            postData[csrfName] = UsersStore.CSRF;
            $.ajax({
              url: "admin/modifyUser",
              dataType: "text",
              type: "POST",
              data: postData,
              success: function(data){
                setConfigurationMessageCallback("user modified");
                ReloadAdminPanelUserList();
              }.bind(this),
              error: function(xhr, status, err){
                setConfigurationMessageCallback(xhr.responseText);
              }
             });
        }
    },
    render: function(){
        var usersList = this.state.allUsers.map(function(user, index){
            return(
                <option value={JSON.stringify(user)} key={index} onClick={this.showUserDetails}>{user.login}</option>
            )
        }.bind(this));
        var roleOptions = AvailableRoles.map(function(role, index){
            return(
                <option value={role.roleType} key={index}>{role.name}</option>
            )
        });
        var enabledOptions = AvailableEnabledValues.map(function(option, index){
            return(
                <option value={option.value} key={index}>{option.name}</option>
            )
        });
        return(
            <div className="userModifyForm">
                <h1>Modify user</h1>
                <div className="row">
                    <div className="col-xs-8">
                        <select ref="user" name="user" size={this.state.allUsers.length}>
                            {usersList}
                        </select>
                    </div>
                    <div className="col-xs=4">
                        <table>
                            <tbody>
                                <tr><td style={TableCellStyle}>
                                    <input style={InputStyle} type="text" value="" ref="userName" name="userName" placeholder="user name" />
                                </td></tr>
                                <tr><td style={TableCellStyle}>
                                    <input style={InputStyle} type="password" ref="password" name="password" placeholder="new password" />
                                </td></tr>
                                <tr><td style={TableCellStyle}>
                                    <select ref="selectedUserRole" name="selectedUserRole">
                                        {roleOptions}
                                    </select>
                                </td></tr>
                                <tr><td style={TableCellStyle}>
                                    <select ref="selectedUserEnabled" name="selectedUserEnabled">
                                        {enabledOptions}
                                    </select>
                                </td></tr>
                                <tr><td style={TableCellStyle}>
                                    <button type="button" className="btn btn-default" onClick={this.deleteUser}>delete user</button>
                                </td><td>
                                    <button type="button" className="btn btn-default" onClick={this.modifyUser}>modify user</button>
                                </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        )
    }
});

var UserAddForm = React.createClass({
    handleAddUser: function(){
        var userName = this.refs.userName.value;
        var password = this.refs.password.value;
        var role = this.refs.role.value;
        var postData = {
          userName: userName,
          password: password,
          role: role
        }
        postData[csrfName] = UsersStore.CSRF;
        $.ajax({
          url: "admin/addUser",
          dataType: "text",
          type: "POST",
          data: postData,
          success: function(data){
            setConfigurationMessageCallback("user added");
            ReloadAdminPanelUserList();
          }.bind(this),
          error: function(xhr, status, err){
            setConfigurationMessageCallback(xhr.responseText);
          }
        })
    },
    handleBackButton: function(){
        unmountContent();
        renderMainView();
    },
    render: function(){
        var roleOptions = AvailableRoles.map(function(role, index){
            return(
                <option value={role.roleType} key={index}>{role.name}</option>
            )
        });
        return(
            <div className="adminPanelView">
                <h1>Add user</h1>
                <table>
                    <tbody>
                        <tr><td style={TableCellStyle}>
                            <input style={InputStyle} type="text" ref="userName" name="userName" placeholder="user name" />
                        </td></tr>
                        <tr><td style={TableCellStyle}>
                            <input style={InputStyle} type="password" ref="password" name="password" placeholder="password" />
                        </td></tr>
                        <tr><td style={TableCellStyle}>
                            Role: <select ref="role" name="role" defaultValue="ROLE_USER">{roleOptions}</select>
                        </td></tr>
                        <tr><td style={TableCellStyle}>
                            <button type="button" className="btn btn-default" onClick={this.handleAddUser} >add user</button>
                        </td></tr>
                    </tbody>
                </table>
            </div>
        )
    }
})

var PasswordChangeView = React.createClass({
    handleBackButton: function(){
        unmountContent();
        renderMainView();
    },
    handlePasswordChange: function(){
        var postData = {
            oldPassword: this.refs.oldPassword.value,
            newPassword: this.refs.newPassword.value
        }
        postData[csrfName] = UsersStore.CSRF;
        $.ajax({
          url: "changePassword",
          dataType: "text",
          type: "POST",
          data: postData,
          success: function(data){
            setConfigurationMessageCallback("password changed");
          },
          error: function(xhr, status, err){
            setConfigurationMessageCallback(xhr.responseText);
          }
        });
    },
    render: function(){
        return(
            <div style={FormStyle} className="passwordChangeView">
                <button type="button" className="btn btn-link" onClick={this.handleBackButton}>Back to main view</button>
                <h1>Change password</h1>
                <table>
                    <tbody>
                        <tr><td style={TableCellStyle}>
                            <input style={InputStyle} type="password" ref="oldPassword" name="oldPassword" placeholder="old password" />
                        </td></tr>
                        <tr><td style={TableCellStyle}>
                            <input style={InputStyle} type="password" ref="newPassword" name="newPassword" placeholder="new password" />
                        </td></tr>
                        <tr><td style={TableCellStyle}>
                            <button type="button" className="btn btn-default" onClick={this.handlePasswordChange} >change</button>
                        </td></tr>
                    </tbody>
                </table>
                <ConfigurationMessageBox/>
            </div>
        )
    }
})

var NavbarButtons = React.createClass({
    componentDidMount: function(){
        navbarButtonsMounted = true;
    },

    render: function(){
        if(this.props.role == "ROLE_ADMIN"){
            return(
                <div className="navbarButtons">
                    <LogoutButton/>
                    <ChangePasswordButton />
                    <AdminPanelButton />
                </div>
            )
        }else{
            return(
                <div className="navbarButtons">
                    <LogoutButton/>
                    <ChangePasswordButton />
                </div>
            )
        }
    }
});

var AdminPanelButton = React.createClass({
    handleOnClick: function(){
        unmountContent();
        mountAdminPanelView();
    },
    render: function(){
        return(
            <div className="adminPanelButton">
                <ul className="nav navbar-nav navbar-right">
                    <li>
                        <a style={NavButtonStyle} onClick={this.handleOnClick}>Admin Panel</a>
                    </li>
                </ul>
            </div>
        )
    }
});

var ChangePasswordButton = React.createClass({
    handleOnClick: function(){
        unmountContent();
        mountPasswordChangeView();
    },
    render: function(){
        return(
            <div className="changePasswordButton">
                <ul className="nav navbar-nav navbar-right">
                    <li>
                        <a style={NavButtonStyle} onClick={this.handleOnClick}>Change password</a>
                    </li>
                </ul>
            </div>
        )
    }
});

var LogoutButton = React.createClass({
    handleOnClick: function(){
        WsConnector.disconnect();
        $.ajax({
            url: "logout",
            dataType: 'text',
            cache: false,
            success: function(data) {
                unmountContent();
                unmountNavbar();
                renderMainView();
            },
            error: function(xhr, status, err) {
                alert(xhr.responseText);
            }
        });
    },
    render: function(){
        return(
            <div className="logoutButton">
                <ul className="nav navbar-nav navbar-right">
                    <li>
                        <a style={NavButtonStyle} onClick={this.handleOnClick}>Logout</a>
                    </li>
                </ul>
            </div>
        )
    }
})

var UsersOnlineView = React.createClass({
    mixins: [Reflux.connect(UsersStore,"list")],

    componentDidMount: function(){
    MessageActions.getAllMessages();
    setTimeout(function(){
            UsersActions.getAllUsers();
        }, 1000);
    },

    render: function(){
        var users = "";
        if(this.state.list){
            users = this.state.list.map(function(user, index){
                return user
            });
        }
        return(
            <div className="usersOnlineView">
                <textarea style={textAreaStyle} value={users.join("\n")} rows="20" readOnly />
            </div>
        )
    }

});

var Message = React.createClass({
    render: function(){
        return(
          <div className="message">
            {this.props.author} : {this.props.body}
          </div>
        )
    }
});

var textAreaStyle = { resize: "none", width: "100%" };

var ChatMessageBox = React.createClass({
    mixins: [Reflux.connect(MessagesStore,"list")],
    scrollDown: function(){
        if( this.refs.textArea ){
            this.refs.textArea.scrollTop = this.refs.textArea.scrollHeight;
        }
    },
    componentDidUpdate: function(){
        this.scrollDown();
    },
    render: function(){
        var messages = "";
        if(this.state.list){
            messages = this.state.list.map(function(msg, index){
                return msg;
            });
        }
        return(
            <div className="chatMessageBox">
                <textarea style={textAreaStyle} ref="textArea" rows="20" cols="100" value={messages.join("\n")} readOnly />
            </div>
        )
    }

});

var msgInputStyle = { width: "100%" };

var InputMessageBox = React.createClass({
    handleInputKeyup: function(e){
        if(e.keyCode == 13){
            MessageActions.sendMessage(e.target.value);
            this.refs.msgInput.value = "";
        }
    },
    componentDidMount: function(){
        if( this.refs.msgInput ){
            this.refs.msgInput.focus();
        }
    },
    render: function(){
        return(
            <div className="inputMessageBox">
                <input style={msgInputStyle} ref="msgInput" name="msgInput" type="text" onKeyUp={this.handleInputKeyup} />
            </div>
        )
    }

});

var chatTableStyle = { marginTop: "15%" }

var MainChatView = React.createClass({
    componentDidMount: function(){
        WsConnector.connect();
        mountNavbarButtons();
    },
    render: function(){
        return(
            <div className="mainChatView">
                <table style={chatTableStyle}>
                    <tbody>
                        <tr>
                            <td colSpan="2">
                                <ChatMessageBox />
                            </td>
                            <td>
                                <UsersOnlineView />
                            </td>
                        </tr>
                        <tr>
                            <td colSpan="3">
                                <InputMessageBox />
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        )
    }
});

var messageBoxStyle = {
    color: "red"
};

var LoginMessageBox = React.createClass({
    componentDidMount: function(){
        setLoginMessageCallback = this.setMessage;
    },
    getInitialState: function(){
        return {message: ""};
    },
    setMessage: function(message){
        this.setState({message: message});
    },
    render: function(){
        return (
            <div className="loginMessageBox" style={messageBoxStyle}>
                {this.state.message}
            </div>
        )
    }
});

var ConfigurationMessageBox = React.createClass({
    componentDidMount: function(){
        setConfigurationMessageCallback = this.setMessage;
    },
    getInitialState: function(){
        return {message: ""};
    },
    setMessage: function(message){
        this.setState({message: message});
    },
    render: function(){
        return (
            <div className="ConfigurationMessageBox" style={messageBoxStyle}>
                {this.state.message}
            </div>
        )
    }
});

var LoginForm = React.createClass({
  forwardToChatView: function(data){
    unmountContent();
    mountChatView(data);
  },
  postCredentials: function(login, password, csrfName, csrfValue){
    var postData = {
      login: login,
      password: password
    }
    postData[csrfName] = csrfValue;
    $.ajax({
      url: "login",
      dataType: "html",
      type: "POST",
      data: postData,
      success: function(data){
        this.forwardToChatView(data);
      }.bind(this),
      error: function(xhr, status, err){
        setLoginMessageCallback("login failed");
      }
    })
  },
  login: function(){
    var login = ReactDOM.findDOMNode(this.refs.login).value.trim();
    var password = ReactDOM.findDOMNode(this.refs.password).value.trim();
    this.postCredentials(login, password, csrfName, csrfValue);
  },
  render: function(){
    return(
      <div style={FormStyle} className="loginForm">
        <table>
          <tbody>
          <tr><td style={TableCellStyle}>
            <input style={InputStyle} ref="login" type="text" placeholder="login" name="login"/>
          </td></tr>
          <tr><td style={TableCellStyle}>
            <input style={InputStyle} ref="password" type="password" placeholder="password" name="password" />
          </td></tr>
          <tr><td style={TableCellStyle}>
            <button type="button" className="btn btn-primary" onClick={this.login}>Login</button>
          </td></tr>
          </tbody>
        </table>
        <LoginMessageBox/>
      </div>
    )
  }
});

exports.render = function(){
  renderMainView();
};
