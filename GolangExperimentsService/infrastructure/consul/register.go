package consul

import (
	"fmt"
	"net"

	"github.com/hashicorp/consul/api"
	"github.com/segmentio/ksuid"
)

// Client provides an interface for communicating with registry
type Client struct {
	*api.Client
}

// NewClient returns a new Client with connection to consul
func NewClient(addr string) (*Client, error) {
	cfg := api.DefaultConfig()
	cfg.Address = addr

	c, err := api.NewClient(cfg)
	if err != nil {
		return nil, err
	}

	return &Client{c}, nil
}

// Register a service with registry
func (c *Client) Register(name string, port int) (string, error) {
	conn, err := net.Dial("udp", "8.8.8.8:80")
	if err != nil {
		return "", fmt.Errorf("unable to determine local addr: %v", err)
	}
	defer conn.Close()

	var (
		uuid      = fmt.Sprintf("%s-%s", name, ksuid.New().String())
		localAddr = conn.LocalAddr().(*net.UDPAddr)
	)

	healthURL := fmt.Sprintf("http://%s:8123/health", localAddr.IP.String())

	reg := &api.AgentServiceRegistration{
		ID:      uuid,
		Name:    name,
		Port:    port,
		Address: localAddr.IP.String(),
		Tags: []string{
			fmt.Sprintf("urlprefix-/%s", name),
			"urlprefix-/mgmt/v1/accounts",
		},
		Check: &api.AgentServiceCheck{
			Method:   "GET",
			Timeout:  "20s",
			Interval: "1m",
			HTTP:     healthURL,
			Name:     "HTTP check for service",
		},
	}

	return uuid, c.Agent().ServiceRegister(reg)
}

// Deregister removes the service address from registry
func (c *Client) Deregister(id string) error {
	return c.Agent().ServiceDeregister(id)
}
