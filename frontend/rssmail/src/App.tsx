import './App.scss';
import { Routes, BrowserRouter as Router, Route } from 'react-router-dom'
import { Header } from './components/Header';
import { CreateSubscription } from './components/CreateSubscription';
import { ValidateSubscription } from './components/ValidateSubscription';

const App = () => {
  return (
    <div className="App">
      <Header />
      <Router>
          <Routes>
              {/* <Route path="/secret/owner/destroy/:keyName">
                  <SecretDestroy showSpinner={setShowSpinner} />
              </Route>
              <Route path="/secret/owner/:keyName">
                  <SecretCreated />
              </Route>
              <Route path={['/secret/:keyName', '/s/:keyName']}>
                  <ConsumeSecret showSpinner={setShowSpinner} />
              </Route>
              <Route path="/security">
                  <Security />
              </Route> */}
              <Route path="/validatesubscription" element={<ValidateSubscription />} />
              <Route path="/" element={<CreateSubscription />} />
          </Routes>
      </Router>
      {/* <Spinner
          style={{ position: 'fixed', top: '50%', left: '50%' }}
          animation="border"
          role="status"
          hidden={!showSpinner}
      /> */}
      {/* <Footer /> */}
    </div>
  );
}

export default App;