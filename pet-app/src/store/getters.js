const getters = {
  size: state => state.app.size,
  device: state => state.app.device,
  dict: state => state.dict.dict,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  id: state => state.user.id,
  name: state => state.user.name,
  roles: state => state.user.roles,
  menu: state => state.app.menu,
}
export default getters
