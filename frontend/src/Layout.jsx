import {Outlet} from 'react-router-dom';
import Navbar from './components/Navbar';
import Footer from './components/Footer';

function Layout(){
    return(
      <div className="layout">
          <Navbar/>
          <main className="main">
              <Outlet/>
          </main>
          <Footer/>
      </div>
    );
}

export default Layout;
