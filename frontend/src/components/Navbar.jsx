import {NavLink} from "react-router-dom";

function Navbar(){
    return(
        <nav style={styles.nav}>
            <div>
                <div>
                    Dalgona
                    <NavLink to="/login">Login</NavLink>
                    <NavLink to="/register">Register</NavLink>
                </div>
            </div>
        </nav>
    );
}

const styles = {
    nav: {
        display: 'flex',
        justifyContent: 'space-between'
    }
}

export default Navbar;