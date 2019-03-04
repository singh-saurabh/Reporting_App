# Steps to deploy BlockChain nodes:

* First update your system `sudo apt update`
* Then install pip and libssl-dev `sudo apt -y install python3-pip libssl-dev`
* Then install bigchaindb using __pip__ `sudo pip3 install bigchaindb==2.0.0b9`
* Configure bigchainDB server using `bigchaindb configure`
* Install __MongoDB__ : `sudo apt install mongodb`
* Install unzip : `sudo apt install -y unzip`
* Now install __tendermint__ using following commands: 
`wget https://github.com/tendermint/tendermint/releases/download/v0.22.8/tendermint_0.22.8_linux_amd64.zip
unzip tendermint_0.22.8_linux_amd64.zip
rm tendermint_0.22.8_linux_amd64.zip
sudo mv tendermint /usr/local/bin`
* Configure __tendermint__ : `tendermint init`
* Each node is denoted by its __hostname__, __pub_key.value__, __node_id__.
* __pub_key__ is stored in `$HOME/.tendermint/config/priv_validator.json`
* To get __node_id__ run: `tendermint show_node_id`
* Create __genesis.json__ located in `$HOME/.tendermint/config/genesis.json` and share with other nodes in the network
* Edit the `$HOME/.tendermint/config/config.toml` file
* Start __MongoDB__ (if not started when you installed)
* Monitoring using monit can be done.
* Install using : `sudo apt install monit`
* `bigchaindb-monit-config
* monit -d <number_of_seconds>`
* Refresh the MongoDB using :
`bigchaindb drop`
`tendermint unsafe_reset_all`
`rm -r $HOME/.tendermint`

# Steps to setup django server
* First create a `virtualenv` with __python3.6__ and activate it.
* Run migrations using : `./manage.py migrate`
* Run server using : `./manage.py runserver 0.0.0.0:<port>`
