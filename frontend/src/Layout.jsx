import {Outlet} from 'react-router-dom';
import Navbar from './components/Navbar';
import Footer from './components/Footer';

function Layout(){
    return(
      <div style={styles.layout}>
          <Navbar/>
          <main style={styles.main}>
              <Outlet/>
          </main>
          <Footer/>
      </div>
    );
}

const styles = {
    layout: {
        display: 'flex',
        flexDirection: 'column',
        minHeight: '100vh', // Forces the app to fill the whole screen height
    },
    main: {
        flex: 1, // Takes up all remaining space
        padding: '40px',
        display: 'flex',
        justifyContent: 'center', // Centers your Login box horizontally
    }
};
export default Layout;
